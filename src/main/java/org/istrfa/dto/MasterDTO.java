package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MasterDTO {

    private UUID id; // Llave primaria
    private String name; // Nombre de la data
    private String description; // Descripcion de la data
    private Integer prefix; // Prefijo de la data
    private Integer correlative; // Correlativo de la data

}
