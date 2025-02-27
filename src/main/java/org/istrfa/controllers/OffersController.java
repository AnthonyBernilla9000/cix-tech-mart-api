package org.istrfa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.istrfa.dto.*;
import org.istrfa.services.OffersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("offers")
public class OffersController {

    private final OffersService service;

    @Autowired
    public OffersController(OffersService service) {
        this.service = service;
    }


    @Operation(summary = "Obtiene los registros de la tabla Ofertas a partir de filtros.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna los registros de la tabla Ofertas de acuerdo a los filtros proporcionados.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener los registros", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/bandeja")
    public ResponseEntity<Page<OffersTrayDTO>> getBandeja(
            OffersFilterDTO filtro,
            @RequestParam Integer page,
            @RequestParam Integer size
    ) {
        try {
            return ResponseEntity.ok().body(service.bandeja(filtro, page, size));
        } catch (Exception e) {
            log.error("Hubo un error al obtener las ofertas: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @Operation(summary = "Guarda la información de las ofertas.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna un responseDTO con la información de la oferta guardada.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class))),
                    @ApiResponse(responseCode = "417", description = "Error al guardar la información", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<ResponseDTO<OffersDTO>> save(@RequestBody OffersPostDTO postdto) {
        try {
            return ResponseEntity.ok().body(service.save(postdto));
        } catch (Exception e) {
            log.error("Hubo un error al guardar la información de la oferta : ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @Operation(summary = "Cambia el estado a enviado de una orden.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna un response confirmando del envio.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class))),
                    @ApiResponse(responseCode = "417", description = "Error al enviar la orden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @PutMapping("/state")
    public ResponseEntity<ResponseDTO<Integer>> sendOrden(
            @RequestParam(name = "idoffer") UUID idoffer,
            @RequestParam(name = "state") UUID state
    ) {
        try {
            return ResponseEntity.ok().body(service.updateState(idoffer, state));
        } catch (Exception e) {
            log.error("Hubo un error al actualizar el estado de la oferta: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }


}
