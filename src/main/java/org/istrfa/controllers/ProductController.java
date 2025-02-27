package org.istrfa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.istrfa.dto.*;
import org.istrfa.services.ProductService;
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
@RequestMapping("product")
public class ProductController {

    private final ProductService service;

    @Autowired
    public ProductController(ProductService service) {
        this.service = service;
    }

    @Operation(summary = "Obtiene los registros de la tabla Producto a partir de filtros.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna los registros de la tabla Producto de acuerdo a los filtros proporcionados.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener los registros", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/bandeja")
    public ResponseEntity<Page<ProductTrayDTO>> getBandeja(
            ProductFilterDTO filtro,
            @RequestParam Integer page,
            @RequestParam Integer size
    ) {
        try {
            return ResponseEntity.ok().body(service.bandeja(filtro, page, size));
        } catch (Exception e) {
            log.error("Hubo un error al obtener los productos: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @Operation(summary = "Guarda la información de los productos.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna un responseDTO con la información del producto guardado.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class))),
                    @ApiResponse(responseCode = "417", description = "Error al guardar la información", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<ResponseDTO<ProductDTO>> save(@RequestBody ProductPostDTO productpost) {
        try {
            return ResponseEntity.ok().body(service.save(productpost));
        } catch (Exception e) {
            log.error("Hubo un error al guardar la información del producto : ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }


    @Operation(summary = "Obtiene la información de un Producto a partir de su id.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna la información de un producto de acuerdo al id proporcionado.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ClientDTO.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener la información", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> findById(
            @PathVariable(name = "id") UUID id
    ) {
        try {
            return ResponseEntity.ok().body(service.findByid(id));
        } catch (Exception e) {
            log.error("Hubo un error al obtener el producto por el id: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @Operation(summary = "Realiza el eliminado lógico de un Producto a partir de su id.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna un objeto con un mensaje confirmando la eliminación.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class))),
                    @ApiResponse(responseCode = "417", description = "Error al eliminar el producto", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Integer>> deleteById(
            @PathVariable(name = "id") UUID id
    ) {
        try {
            return ResponseEntity.ok().body(service.deleteProduct(id));
        } catch (Exception e) {
            log.error("Hubo un error al eliminar el producto por el id: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @Operation(summary = "Obtiene los registros de la tabla Producto que estan en tendencia.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna los registros de la tabla Producto que se encuentren en tendencia",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = List.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener los registros", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/trendings")
    public ResponseEntity<List<ProductDTO>> getProductTrending() {
        try {
            return ResponseEntity.ok().body(service.getProductTrending());
        } catch (Exception e) {
            log.error("Hubo un error al obtener los productos en tendencia: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @Operation(summary = "Obtiene los registros de la tabla Producto que estan en oferta.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna los registros de la tabla Producto que se encuentren en oferta",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = List.class))),
                    @ApiResponse(responseCode = "417", description = "Error al obtener los registros", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/offers")
    public ResponseEntity<List<ProductDTO>> getProductOffers() {
        try {
            return ResponseEntity.ok().body(service.getProductOffers());
        } catch (Exception e) {
            log.error("Hubo un error al obtener los productos en oferta: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }


}
