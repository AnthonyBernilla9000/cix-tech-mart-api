package org.istrfa.controllers;

import lombok.extern.slf4j.Slf4j;
import org.istrfa.dto.ResponseDTO;
import org.istrfa.dto.SessionStripeDTO;
import org.istrfa.services.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("stripe")
public class StripeController {

    // La clave secreta de Stripe se obtiene del archivo de configuración
    @Value("${stripe.key-secret}")
    private String stripeApiKey;


    private final StripeService service;

    @Autowired
    public StripeController(StripeService service) {
        this.service = service;
    }



    @PostMapping("/create-checkout-session")
    public ResponseEntity<ResponseDTO<String>> createCheckoutSession(
            @RequestParam UUID idOrden,
            @RequestParam String urlreturn) {
        try {
            return ResponseEntity.ok().body(service.createCheckoutSession(idOrden,urlreturn));
        } catch (Exception e) {
            log.error("Hubo un error al crear la sesión con stripe: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @GetMapping("/session")
    public ResponseEntity<?> getSessionDetails(
            @RequestParam String sessionId,
            @RequestParam(required = false) UUID idOrden,
            @RequestParam(required = false,defaultValue = "false") Boolean update) {
        try {
            return ResponseEntity.ok().body(service.getSessionDetails(sessionId,idOrden,update));
        } catch (Exception e) {
            log.error("Hubo un error al obtener la sesión de stripe: ", e);
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(errorResponse);
        }
    }


}
