package org.istrfa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.istrfa.dto.ClientDTO;
import org.istrfa.dto.PersonDTO;
import org.istrfa.dto.ResponseDTO;
import org.istrfa.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("person")
public class PersonController {

    private final PersonService service;

    @Autowired
    public PersonController(PersonService service) {
        this.service = service;
    }

    @Operation(summary = "Obtiene la información de una persona por el número de documento.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna la información de una persona de acuerdo al número de documento proporcionado.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ClientDTO.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener la información", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/numdoc")
    public ResponseEntity<ResponseDTO<PersonDTO>> findByNumerodoc(
            @RequestParam String numerodoc,
            @RequestParam(required = false) UUID typedoc) {
        try {
            return ResponseEntity.ok().body(service.findByNumerodoc(numerodoc, typedoc));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }
}
