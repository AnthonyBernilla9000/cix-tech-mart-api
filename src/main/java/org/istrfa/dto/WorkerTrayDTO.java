package org.istrfa.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class WorkerTrayDTO {

    private UUID id; // Clave primaria
    private String fullname;//Nombre completo de la persona
    private String photo; // Foto del trabajador
    private UUID stateId; // Estado del trabajador
    private String stateName; // Estado de la persona
    private String email; // Email del cliente
    private UUID typeId; // Tipo de usuario
    private String typeName; // Tipo de usuario
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datecreate; // Fecha de creación

    private String numerodoc; // Número de documento

}
