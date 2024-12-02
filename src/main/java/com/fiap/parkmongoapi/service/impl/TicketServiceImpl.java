package com.fiap.parkmongoapi.service.impl;

import com.fiap.parkmongoapi.dto.ticket.CadastroTicketDTO;
import com.fiap.parkmongoapi.dto.ticket.TicketViewDTO;
import com.fiap.parkmongoapi.exception.TicketAlreadyPaidException;
import com.fiap.parkmongoapi.exception.TicketNotFoundException;
import com.fiap.parkmongoapi.model.Motorista;
import com.fiap.parkmongoapi.model.Ticket;
import com.fiap.parkmongoapi.model.Vaga;
import com.fiap.parkmongoapi.model.Veiculo;
import com.fiap.parkmongoapi.model.enums.EnumStatusTicket;
import com.fiap.parkmongoapi.repository.MotoristaRepository;
import com.fiap.parkmongoapi.repository.TicketRepository;
import com.fiap.parkmongoapi.repository.VagaRepository;
import com.fiap.parkmongoapi.repository.VeiculoRepository;
import com.fiap.parkmongoapi.service.TicketService;
import com.fiap.parkmongoapi.utils.TicketUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private VagaRepository vagaRepository;

    @Autowired
    private MotoristaRepository motoristaRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    public Ticket cadastrarTicket(CadastroTicketDTO cadastroTicketDTO) {
        // Valida as entradas e busca as entidades no banco
        Vaga vaga = vagaRepository.findById(cadastroTicketDTO.vagaId())
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));

        Motorista motorista = motoristaRepository.findById(cadastroTicketDTO.motoristaId())
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));

        Veiculo veiculo = veiculoRepository.findById(cadastroTicketDTO.veiculoId())
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

        // Converte a hora atual para o fuso horário de São Paulo
        ZonedDateTime saoPauloTime = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
        LocalDateTime inicio = saoPauloTime.toLocalDateTime();

        // Cria o novo ticket com base no DTO
        Ticket ticket = new Ticket();
        ticket.setVaga(vaga);
        ticket.setMotorista(motorista);
        ticket.setVeiculo(veiculo);
        ticket.setInicio(inicio);
        ticket.setStatus(EnumStatusTicket.EM_ABERTO);

        // Salva o ticket no banco de dados
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket pagarTicket(String ticketId) {
        // Encontra o ticket pelo ID
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket não encontrado"));

        // Verifica se o ticket já foi pago
        if (ticket.getStatus() == EnumStatusTicket.PAGO) {
            throw new TicketAlreadyPaidException("Ticket já foi pago. Data do pagamento: " + ticket.getFim());
        }

        // Obtém a hora de início e a hora atual
        LocalDateTime inicio = ticket.getInicio();
        LocalDateTime agora = LocalDateTime.now();

        // Calcula a diferença em horas entre o início e o momento atual
        Duration duration = Duration.between(inicio, agora);
        BigDecimal valorPagamento = TicketUtils.getValorPagamento(duration, ticket);

        // Atualiza o ticket com a hora de fechamento, o valor calculado e a data do pagamento
        ticket.setFim(agora);  // Define a hora de fim
        ticket.setStatus(EnumStatusTicket.PAGO);  // Fecha o ticket
        ticket.setValor(valorPagamento);  // Define o valor a ser pago

        // Salva o ticket atualizado no banco de dados
        return ticketRepository.save(ticket);
    }

    public TicketViewDTO findTicketById(String ticketId) {
        // Primeiro, encontramos o ticket pelo ID
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket não encontrado"));

        // Buscando os dados relacionados (Motorista, Veículo, Vaga)
        Motorista motorista = ticket.getMotorista();
        Veiculo veiculo = ticket.getVeiculo();
        Vaga vaga = ticket.getVaga();

        // Mapear as informações para o DTO
        return new TicketViewDTO(
                ticket.getId(),
                ticket.getInicio(),
                ticket.getFim(),
                ticket.getStatus(),
                ticket.getValor(),
                motorista.getCpf(),
                motorista.getNome(),
                veiculo.getPlaca(),
                veiculo.getModelo(),
                veiculo.getTipoVeiculo(),
                vaga.getLocId(),
                vaga.getTipoVeiculo(),
                vaga.getEndereco()
        );
    }
}



