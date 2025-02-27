package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ClientPostDTO {

    private UUID id; // Clave primaria
    private String email; // Email del cliente
    private String idfirebase; // Id del cliente en firebase
    private String photo; // Foto del cliente
    private String fullname; // Nombre completo del cliente

    //Data al actualizar
    private String name; // Email del cliente
    private String lastname; // Apellidos del cliente
    private UUID districtId; //Distrito del cliente
    private String address; // Dirección del cliente
    private String phone; // Dirección del cliente



}
