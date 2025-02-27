package org.istrfa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.istrfa.dto.*;
import org.istrfa.services.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("refund")
public class RefundController {

    private final RefundService service;

    @Autowired
    public RefundController(RefundService service) {
        this.service = service;
    }


    @Operation(summary = "Obtiene los registros de la tabla Reembolsos a partir de filtros.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna los registros de la tabla Reembolsos de acuerdo a los filtros proporcionados.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener los registros", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/bandeja")
    public ResponseEntity<Page<RefundTrayDTO>> getBandeja(
            RefundFilterDTO filtro,
            @RequestParam Integer page,
            @RequestParam Integer size
    ) {
        try {
            return ResponseEntity.ok().body(service.bandeja(filtro, page, size));
        } catch (Exception e) {
            log.error("Hubo un error al obtener los clientes: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }


    @Operation(summary = "Guarda la información de un reembolso.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna un responseDTO confirmando la solicitud del reembolso.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class))),
                    @ApiResponse(responseCode = "417", description = "Error al guardar la información", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<ResponseDTO<Integer>> save(@RequestBody RefundPostDTO dto) {
        try {
            return ResponseEntity.ok().body(service.generateSolRefund(dto));
        } catch (Exception e) {
            log.error("Hubo un error al guardar la información del reembolso: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @Operation(summary = "Evaluar la solicitud de reembolso.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna un responseDTO confirmando la evaluación de la solicitud del reembolso.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class))),
                    @ApiResponse(responseCode = "417", description = "Error al guardar la información", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @PostMapping("/evaluate")
    public ResponseEntity<ResponseDTO<Integer>> evaluate(
            @RequestBody RefundEvaluateDTO dto
    ) {
        try {
            return ResponseEntity.ok().body(service.evaluateSolRefund(dto));
        } catch (Exception e) {
            log.error("Hubo un error al guardar la información de la evaluación del reembolso: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

}
