package com.fiap.parkmongoapi.controller;

import com.fiap.parkmongoapi.dto.motorista.AtualizaMotoristaDTO;
import com.fiap.parkmongoapi.dto.motorista.CadastroMotoristaDTO;
import com.fiap.parkmongoapi.dto.motorista.MotoristaResponseDTO;
import com.fiap.parkmongoapi.model.Motorista;
import com.fiap.parkmongoapi.service.MotoristaService;
import com.fiap.parkmongoapi.validations.ValidCpf;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;

@RestController
@RequestMapping(value = "/motorista")
@RequiredArgsConstructor
@Validated
@Tag(name = "Motorista", description = "API para gerenciar motoristas")
public class MotoristaController {

    private final MotoristaService motoristaService;

    @GetMapping("/{cpf}")
    @Operation(summary = "Consultar Motorista por CPF",
            description = "Retorna um motorista com base no CPF fornecido.")
    public ResponseEntity<MotoristaResponseDTO> consultarMotoristaPorCpf(
            @PathVariable @Valid @ValidCpf String cpf){

        var motorista = this.motoristaService.consultarMotoristaPorCpf(cpf);
        var dto = MotoristaResponseDTO.toDTO(motorista);

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @Operation(summary = "Cadastrar Motorista", description = "Cadastra um novo motorista.")
    public ResponseEntity<MotoristaResponseDTO> cadastrarMotorista(
            @Valid @RequestBody CadastroMotoristaDTO motoristaDTO){

        var motorista = motoristaService.cadastrarMotorista(motoristaDTO.toEntity());
        var dto = MotoristaResponseDTO.toDTO(motorista);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{cpf}")
                .buildAndExpand(motorista.getCpf())
                .toUri();

        return ResponseEntity.created(location).body(dto);
    }

    @PutMapping("/{cpf}")
    @Operation(summary = "Atualizar Motorista", description = "Atualiza os dados de um motorista.")
    public ResponseEntity<MotoristaResponseDTO> atualizarMotorista(
            @PathVariable @Valid @ValidCpf String cpf,
            @RequestBody @Valid AtualizaMotoristaDTO atualizacaoMotorista) {

        var motorista = new Motorista(cpf, atualizacaoMotorista.nome(),
                atualizacaoMotorista.dataNascimento(), atualizacaoMotorista.email(),
                atualizacaoMotorista.perfil(),
                new ArrayList<>());

        var motoristaAtualizado = motoristaService.atualizarMotorista(cpf,motorista);
        var dto = MotoristaResponseDTO.toDTO(motoristaAtualizado);


        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{cpf}")
    @Operation(summary = "Deletar Motorista", description = "Deleta um motorista.")
    public ResponseEntity<Void> deletarMotorista(
            @PathVariable @Valid @ValidCpf String cpf){

        this.motoristaService.deletarMotorista(cpf);

        return ResponseEntity.noContent().build();
    }



}
