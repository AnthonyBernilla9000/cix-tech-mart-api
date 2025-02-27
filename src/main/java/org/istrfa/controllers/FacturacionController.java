package org.istrfa.controllers;

import lombok.extern.slf4j.Slf4j;
import org.istrfa.dto.FacturacionDetalleDTO;
import org.istrfa.dto.SunatResponseDTO;
import org.istrfa.sunat.LiFacManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("billing")
public class FacturacionController {

    private final LiFacManager service;

    @Autowired
    public FacturacionController(LiFacManager service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SunatResponseDTO> sendSunat(@RequestBody FacturacionDetalleDTO dto) {
        try {
            return ResponseEntity.ok().body(service.createInvoice(dto));
        } catch (Exception e) {
            log.info("Error al generar la factura en sunat: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

}
