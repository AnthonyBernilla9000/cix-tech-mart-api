package org.istrfa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.istrfa.dto.*;
import org.istrfa.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("client")
public class ClientController {

    private final ClientService service;

    @Autowired
    public ClientController(ClientService service) {
        this.service = service;
    }

    @Operation(summary = "Obtiene los registros de la tabla Cliente a partir de filtros.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna los registros de la tabla Cliente de acuerdo a los filtros proporcionados.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener los registros", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/bandeja")
    public ResponseEntity<Page<ClientTrayDTO>> getBandeja(
            ClientFilterDTO filtro,
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

    @Operation(summary = "Obtiene la información de un Cliente a partir de su id.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna la información de un cliente de acuerdo al id proporcionado.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ClientDTO.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener la información", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> findById(
            @PathVariable(name = "id") UUID id
    ) {
        try {
            return ResponseEntity.ok().body(service.findByid(id));
        } catch (Exception e) {
            log.error("Hubo un error al obtener el cliente por el id: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @Operation(summary = "Guarda la información de un cliente.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna un responseDTO con la información del cliente guardado.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class))),
                    @ApiResponse(responseCode = "417", description = "Error al guardar la información", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<ResponseDTO<ClientDTO>> save(@RequestBody ClientPostDTO newUser) {
        try {
            return ResponseEntity.ok().body(service.save(newUser));
        } catch (Exception e) {
            log.error("Hubo un error al guardar la información del cliente : ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

//    @Operation(summary = "Actualiza la información de un Cliente a partir de su id.")
//    @ApiResponses(
//            value = {
//                    @ApiResponse(responseCode = "200", description = "Retorna la información de un cliente actualizado.",
//                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ClientDTO.class))),
//                    @ApiResponse(responseCode = "417", description = "Error al actualizar la información", content = @Content),
//                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
//            }
//    )
//    @PutMapping("/{id}")
//    public ResponseEntity<ClientDTO> updateClient(
//            @PathVariable(name = "id") UUID id,
//            @RequestBody ClientPostDTO dto
//    ) {
//        try {
//            return ResponseEntity.ok().body(service.updateClient(id, dto));
//        } catch (Exception e) {
//            log.error("Hubo un error al actalizar el cliente por el id: ", e);
//            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
//        }
//    }

    @Operation(summary = "Obtiene la información de un Cliente a partir de su email.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna la información de un cliente de acuerdo al email proporcionado.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ClientDTO.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener la información", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/email")
    public ResponseEntity<ClientDTO> findByEmail(
            @RequestParam String email
    ) {
        try {
            return ResponseEntity.ok().body(service.findByEmail(email));
        } catch (Exception e) {
            log.error("Hubo un error al obtener el cliente por el email: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

}
