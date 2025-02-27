package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ClientFilterDTO {
    private String fullname; // Nombre completo del cliente
    private String email; // Email del cliente
    private UUID stateId; // Estado de la persona

}
