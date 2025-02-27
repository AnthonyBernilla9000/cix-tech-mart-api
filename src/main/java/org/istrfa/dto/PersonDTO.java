package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PersonDTO {

    private UUID id;
    private UUID typedocumentId; // Relación con la tabla tbl_data_mast (Tipo de documento)
    private String numerodoc; // Número de documento
    private String name; // Nombre de la persona
    private String firstname; // Primer apellido
    private String lastname; // Segundo apellido
    private String address; // Dirección de la persona
    private String phone; // Teléfono de la persona
    private String fullname;//Nombre completo
}
