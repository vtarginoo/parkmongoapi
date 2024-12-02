package com.fiap.parkmongoapi.controller;

import com.fiap.parkmongoapi.dto.motorista.AtualizaMotoristaDTO;
import com.fiap.parkmongoapi.dto.motorista.CadastroMotoristaDTO;
import com.fiap.parkmongoapi.model.Motorista;
import com.fiap.parkmongoapi.service.MotoristaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;

@RestController
@RequestMapping(value = "/motorista")
@Tag(name = "Motorista", description = "API para gerenciar motoristas")
public class MotoristaController {

    @Autowired
    private MotoristaService motoristaService;

    @GetMapping("/{cpf}")
    @Operation(summary = "Consultar Motorista por CPF",
            description = "Retorna um motorista com base no CPF fornecido.")
    public ResponseEntity<Motorista> consultarMotoristaPorCpf(
            @PathVariable
            @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos.")
            String cpf){

        var motorista = this.motoristaService.consultarMotoristaPorCpf(cpf);

        return ResponseEntity.ok(motorista);
    }

    @PostMapping
    @Operation(summary = "Cadastrar Motorista", description = "Cadastra um novo motorista.")
    public ResponseEntity<Motorista> cadastrarMotorista(
            @Valid @RequestBody CadastroMotoristaDTO motoristaDTO){

        var motorista = motoristaService.cadastrarMotorista(motoristaDTO.toEntity());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{cpf}")
                .buildAndExpand(motorista.getCpf())
                .toUri();

        return ResponseEntity.created(location).body(motorista);
    }

    @PutMapping("/{cpf}")
    @Operation(summary = "Atualizar Motorista", description = "Atualiza os dados de um motorista.")
    public ResponseEntity<Motorista> atualizarMotorista(
            @PathVariable
            @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos.")
            String cpf,
            @RequestBody @Valid AtualizaMotoristaDTO atualizacaoMotorista) {

        var motorista = new Motorista(cpf, atualizacaoMotorista.nome(),
                atualizacaoMotorista.dataNascimento(), atualizacaoMotorista.email(),
                atualizacaoMotorista.perfil(),
                new ArrayList<>());

        var motoristaAtualizado = motoristaService.atualizarMotorista(cpf,motorista);

        return ResponseEntity.ok(motoristaAtualizado);
    }

    @DeleteMapping("/{cpf}")
    @Operation(summary = "Deletar Motorista", description = "Deleta um motorista.")
    public ResponseEntity<String> deletarMotorista(
            @PathVariable
            @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos.")
            String cpf){

        this.motoristaService.deletarMotorista(cpf);

        return ResponseEntity.ok("Motorista com CPF " + cpf + " deletado com sucesso.");
    }



}
