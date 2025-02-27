package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiPeruDni {
    private Boolean success;
    private String dni;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String codVerifica;
}
