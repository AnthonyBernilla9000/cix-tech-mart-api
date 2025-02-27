package org.istrfa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.istrfa.dto.TypeMarcaModeloDTO;
import org.istrfa.services.TypeMarcaModeloService;
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
@RequestMapping("typemarcmodel")
public class TypeMarcaModeloController {

    private final TypeMarcaModeloService service;

    @Autowired
    public TypeMarcaModeloController(TypeMarcaModeloService service) {
        this.service = service;
    }

    @Operation(summary = "Obtiene los tipos de la tabla Tipo marca modelo.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna los tipos de la tabla tipo marca modelo.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = List.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener los tipos", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/types")
    public ResponseEntity<List<TypeMarcaModeloDTO>> getAllTypes() {
        try {
            return ResponseEntity.ok().body(service.getAllTypes());
        } catch (Exception e) {
            log.error("Hubo un error al obtener los tipos: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @Operation(summary = "Obtiene las marcas de la tabla Tipo marca modelo.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna los marcas de la tabla tipo marca modelo.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = List.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener las marcas", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/marcas/{idType}")
    public ResponseEntity<List<TypeMarcaModeloDTO>> getMarcasByType(@PathVariable UUID idType) {
        try {
            return ResponseEntity.ok().body(service.getMarcasByType(idType));
        } catch (Exception e) {
            log.error("Hubo un error al obtener las marcas: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @Operation(summary = "Obtiene los modelos de la tabla Tipo marca modelo.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna los modelos de la tabla tipo marca modelo.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = List.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener los modelos", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/models/{idMarca}")
    public ResponseEntity<List<TypeMarcaModeloDTO>> getModelosByMarca(@PathVariable UUID idMarca) {
        try {
            return ResponseEntity.ok().body(service.getModelosByMarca(idMarca));
        } catch (Exception e) {
            log.error("Hubo un error al obtener los modelos: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }


}
