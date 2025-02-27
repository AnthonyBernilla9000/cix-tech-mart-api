package org.istrfa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.istrfa.dto.DashboardDTO;
import org.istrfa.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("dashboard")
public class DashboardController {

    private final DashboardService service;

    @Autowired
    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @Operation(summary = "Obtiene la información a mostrar en el dashboard.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna la información del dashboard a partir de las fechas dadas.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DashboardDTO.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener la información", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping
    public ResponseEntity<DashboardDTO> getDataDashboard(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime datestart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime dateend,
            @RequestParam(required = false,defaultValue = "5") Integer size

    ) {
        try {
            return ResponseEntity.ok().body(service.getDataDashboard(datestart, dateend,size));
        } catch (Exception e) {
            log.error("Hubo un error al obtener la información en el dashboard: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

}
