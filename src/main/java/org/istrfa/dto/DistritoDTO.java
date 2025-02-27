package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class DistritoDTO {
    private UUID id;
    private String name;
    private String ubigeo;
    private UUID provinceId;  // Relación con la tabla 'tbl_provin'

}
