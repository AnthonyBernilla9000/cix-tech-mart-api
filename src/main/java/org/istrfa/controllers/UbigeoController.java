package org.istrfa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.istrfa.dto.DepartamentoDTO;
import org.istrfa.dto.DistritoDTO;
import org.istrfa.dto.ProvinciaDTO;
import org.istrfa.services.UbigeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("ubigeo")
public class UbigeoController {

    private final UbigeoService service;

    @Autowired
    public UbigeoController(UbigeoService service) {
        this.service = service;
    }


    @Operation(summary = "Obtiene los departamentos de la tabla Departamento.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna los departamentos de la tabla departamento.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = List.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener los departamentos", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/departments")
    public ResponseEntity<List<DepartamentoDTO>> getAllDepartments() {
        try {
            return ResponseEntity.ok().body(service.getAllDepartament());
        } catch (Exception e) {
            log.error("Hubo un error al obtener los departamentos: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @Operation(summary = "Obtiene las provincia de la tabla provincias por el id del departamento.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna las provincias de la tabla provincia.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = List.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener las provincias", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/provinces/{id}")
    public ResponseEntity<List<ProvinciaDTO>> getProvinces(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok().body(service.getProvincesByDepart(id));
        } catch (Exception e) {
            log.error("Hubo un error al obtener las provincias: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @Operation(summary = "Obtiene los distritos de la tabla distritos por el id de la provincia.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna los distritos de la tabla distrito.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = List.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener los distritos", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/districts/{id}")
    public ResponseEntity<List<DistritoDTO>> getDistricts(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok().body(service.getDistritsByProvin(id));
        } catch (Exception e) {
            log.error("Hubo un error al obtener los distritos: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

}
