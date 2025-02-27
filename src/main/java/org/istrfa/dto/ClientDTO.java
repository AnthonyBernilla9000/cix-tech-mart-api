package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ClientDTO {

    private UUID id; // Clave primaria
    private String stateName; // Estado de la persona
    private String email; // Email del cliente
//    private String idfirebase; // Id del cliente en firebase
    private String photo; // Foto del cliente
    private String phone; //Telefono del cliente
    private String name; // Nombre del cliente
    private String lastname; // Apellido paterno del cliente
    private String fullname; // Nombre completo del cliente
    private String address; // Dirección del cliente
    private String districtName; //Distrito del cliente


}
