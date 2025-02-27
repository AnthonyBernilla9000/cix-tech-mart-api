package org.istrfa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.istrfa.dto.MasterDTO;
import org.istrfa.services.MasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("master")
public class MasterController {

    private final MasterService service;

    @Autowired
    public MasterController(MasterService service) {
        this.service = service;
    }


    @Operation(summary = "Obtiene los registros de la tabla Maestro a partir del prefijo y correlativos.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna los registros de la tabla maestro partir del prefijo y correlativos proporcionados.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener los registros", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/find_by_prefix")
    public ResponseEntity<List<MasterDTO>> findByPrefixAndCorrelatives(
            @RequestParam Integer prefix,
            @RequestParam Integer[] correlatives
    ) {
        try {
            if (correlatives.length == 0)
                return ResponseEntity.ok().body(service.findByPrefix(prefix));
            return ResponseEntity.ok().body(service.findByPrefixAndCorrelatives(prefix, correlatives));
        } catch (Exception e) {
            log.error("Hubo un error al obtener los maestro: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }


}
