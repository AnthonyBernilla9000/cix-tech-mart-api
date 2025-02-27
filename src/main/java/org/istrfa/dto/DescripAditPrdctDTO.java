package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class DescripAditPrdctDTO {
    private UUID id;  // Clave primaria
    private String key;  // Campo key
    private String value;  // Campo value
}
