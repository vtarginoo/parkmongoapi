package com.fiap.parkmongoapi.service;

import com.fiap.parkmongoapi.dto.PageResponseDTO;
import com.fiap.parkmongoapi.dto.veiculo.AtualizaVeiculoDTO;
import com.fiap.parkmongoapi.dto.veiculo.VeiculoResponseDTO;
import com.fiap.parkmongoapi.model.Motorista;
import com.fiap.parkmongoapi.model.Veiculo;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface VeiculoService {

    // Cadastrar um veículo
    Motorista cadastrarVeiculo(String cpf, Veiculo veiculo);

    // Atualizar dados de um veículo
    Veiculo atualizarVeiculo(String cpf, String placa, AtualizaVeiculoDTO veiculoAtualizado);

    // Deletar um veículo
    void deletarVeiculo(String cpf, String placa);

    //Deletar todos os Veiculos vinculados a um cpf
    void deleteByCpfMotorista(String cpf);


    // Consultar um veículo pela placa
    Veiculo consultarVeiculoPorPlaca(String placa);

    // Consultar um veículo pela placa e CPF do motorista
    Veiculo consultarVeiculoPorPlacaEMotorista(String cpf, String placa);

    // Consultar um veiculo pelo cpf do motorista
    PageResponseDTO<VeiculoResponseDTO> consultarVeiculosPorMotorista(
            String cpfMotorista, Pageable pageable);
}