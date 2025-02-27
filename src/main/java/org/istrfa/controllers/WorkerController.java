package org.istrfa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.istrfa.dto.*;
import org.istrfa.services.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("worker")
public class WorkerController {

    private final WorkerService service;

    @Autowired
    public WorkerController(WorkerService service) {
        this.service = service;
    }

    @Operation(summary = "Obtiene los registros de la tabla Usuario a partir de filtros.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna los registros de la tabla Usuario de acuerdo a los filtros proporcionados.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener los registros", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/bandeja")
    public ResponseEntity<Page<WorkerTrayDTO>> getBandeja(
            WorkerFilterDTO filtro,
            @RequestParam Integer page,
            @RequestParam Integer size
    ) {
        try {
            return ResponseEntity.ok().body(service.bandeja(filtro, page, size));
        } catch (Exception e) {
            log.error("Hubo un error al obtener los usuarios: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @Operation(summary = "Obtiene la información de un Usuario a partir de su id.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna la información de un usuario de acuerdo al id proporcionado.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = WorkerDTO.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener la información", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<WorkerDTO> findById(
            @PathVariable(name = "id") UUID id
    ) {
        try {
            return ResponseEntity.ok().body(service.findByid(id));
        } catch (Exception e) {
            log.error("Hubo un error al obtener el usuario por el id: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @Operation(summary = "Guarda la información de un usuario.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna un responseDTO con la información del usuario guardado.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class))),
                    @ApiResponse(responseCode = "417", description = "Error al guardar la información", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<ResponseDTO<WorkerDTO>> save(@RequestBody WorkerPostDTO newUser) {
        try {
            return ResponseEntity.ok().body(service.save(newUser));
        } catch (Exception e) {
            log.error("Hubo un error al guardar la información del usuario : ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @Operation(summary = "Obtiene la información de un Usuario a partir de su email.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna la información de un usuario de acuerdo al email proporcionado.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = WorkerDTO.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener la información", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/findemail/{email}")
    public ResponseEntity<WorkerDTO> findById(
            @PathVariable(name = "email") String email
    ) {
        try {
            return ResponseEntity.ok().body(service.findByEmail(email));
        } catch (Exception e) {
            log.error("Hubo un error al obtener el usuario por el correo: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @Operation(summary = "Guarda la información de un usuario.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna un responseDTO con la información del usuario guardado.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class))),
                    @ApiResponse(responseCode = "417", description = "Error al guardar la información", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<Integer>> save(
            @PathVariable(name = "id") UUID id,
            @RequestBody WorkerPostDTO dto
    ) {
        try {
            return ResponseEntity.ok().body(service.updateWorker(id,dto));
        } catch (Exception e) {
            log.error("Hubo un error al actualizar la información del usuario : ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

}
