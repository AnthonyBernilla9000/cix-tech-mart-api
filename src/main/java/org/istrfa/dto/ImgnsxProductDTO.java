package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ImgnsxProductDTO {

    private UUID id;  // Clave primaria
    private String namecolor;  // Nombre del color de la imagen
    private String codecolor;  // Código del color
    private String urlimg;  // URL donde se guardó la imagen


}
