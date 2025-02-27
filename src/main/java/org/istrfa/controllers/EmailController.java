package org.istrfa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.istrfa.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("email")
public class EmailController {

    private final EmailService service;

    @Autowired
    public EmailController(EmailService service) {
        this.service = service;
    }


    @Operation(summary = "Envia un correo electrónico al correo proporcionaado.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna un status 200 indicando que se envio correctamente.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Integer.class))),
                    @ApiResponse(responseCode = "417", description = "Error al enviar el mensaje", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No se encontró la ruta", content = @Content)
            }
    )
    @GetMapping("/send")
    public ResponseEntity<Integer> sendMessage(
            @RequestParam String recipient,
            @RequestParam String sender,
            @RequestParam String subject,
            @RequestParam String content
//            @RequestParam List<MultipartFile> files
    ) {
        try {
            return ResponseEntity.ok().body(service.send(recipient,sender, subject, content));
        } catch (Exception e) {
            log.error("Hubo un error al enviar el mensaje: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

}
