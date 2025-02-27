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
public class WorkerDTO {

    private UUID id; // Clave primaria
    private String photo; // Foto del trabajador
    private UUID stateId; // Estado del trabajador
    private String stateName; // Estado de la persona
    private UUID typeId; // Tipo de usuario
    private String typeName; // Tipo de usuario
    private String authid; // Id del usuario en firebase
    private String email; // Email del cliente
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datecreate; // Fecha de creación


    //Datos de la tabla persona
    private UUID typedocumentId; // Relación con la tabla tbl_data_mast (Tipo de documento)
    private String typedocumentName; // Relación con la tabla tbl_data_mast (Tipo de documento)
    private String numerodoc; // Número de documento
    private String name; // Nombre de la persona
    private String firstname; // Primer apellido
    private String lastname; // Segundo apellido
    private String address; // Dirección de la persona
    private String phone; // Teléfono de la persona
    private String fullname;//Nombre completo de la persona



}
