package org.istrfa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.istrfa.dto.*;
import org.istrfa.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("order")
public class OrderController {

    private final OrderService service;

    @Autowired
    public OrderController(OrderService service) {
        this.service = service;
    }

    @Operation(summary = "Obtiene los registros de la tabla Order a partir de filtros.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna los registros de la tabla Order de acuerdo a los filtros proporcionados.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener los registros", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/bandeja")
    public ResponseEntity<Page<OrderTrayDTO>> getBandeja(
            OrderFilterDTO filtro,
            @RequestParam Integer page,
            @RequestParam Integer size
    ) {
        try {
            return ResponseEntity.ok().body(service.bandeja(filtro, page, size));
        } catch (Exception e) {
            log.error("Hubo un error al obtener los ordenes: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @Operation(summary = "Guarda la información de una orden.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna un responseDTO con la información de la orden guardada.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class))),
                    @ApiResponse(responseCode = "417", description = "Error al guardar la información", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<ResponseDTO<OrderDTO>> save(@RequestBody OrderPostDTO dto) {
        try {
            return ResponseEntity.ok().body(service.saveOrder(dto));
        } catch (Exception e) {
            log.error("Hubo un error al guardar la información de la orden: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @Operation(summary = "Obtiene la información de una orden a partir de su id.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna la información de una orden de acuerdo al id proporcionado.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ClientDTO.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener la información", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> findById(
            @PathVariable(name = "id") UUID id
    ) {
        try {
            return ResponseEntity.ok().body(service.findByid(id));
        } catch (Exception e) {
            log.error("Hubo un error al obtener el orden por el id: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @Operation(summary = "Obtiene la lista de ordenes de un cliente.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna la información de ordenes de un cliente.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = List.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener la información", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/byclient/{clientid}")
    public ResponseEntity<List<OrderTrayDTO>> findByClientId(
            @PathVariable(name = "clientid") UUID clientid
    ) {
        try {
            return ResponseEntity.ok().body(service.getOrdenesByClient(clientid));
        } catch (Exception e) {
            log.error("Hubo un error al obtener las ordenes del cliente: ", e);
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
    @PutMapping("/send/{orderid}")
    public ResponseEntity<ResponseDTO<Integer>> sendOrden(
            @PathVariable(name = "orderid") UUID orderid
    ) {
        try {
            return ResponseEntity.ok().body(service.sendOrder(orderid));
        } catch (Exception e) {
            log.error("Hubo un error al realizar el envio de la orden: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }


    @Operation(summary = "Cancela una orden.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna un response confirmando la cancelación.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class))),
                    @ApiResponse(responseCode = "417", description = "Error al cancelar la orden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/cancel/{orderid}")
    public ResponseEntity<ResponseDTO<Integer>> cancelOrden(
            @PathVariable(name = "orderid") UUID orderid
    ) {
        try {
            return ResponseEntity.ok().body(service.cancelOrder(orderid));
        } catch (Exception e) {
            log.error("Hubo un error al obtener las ordenes del cliente: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }



}
