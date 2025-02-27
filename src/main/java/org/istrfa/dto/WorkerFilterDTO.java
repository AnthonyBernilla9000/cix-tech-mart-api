package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class WorkerFilterDTO {

    private String name; // Nombre de la persona
    private String email; // Email del cliente
    private UUID stateId; // Estado del trabajador


}
