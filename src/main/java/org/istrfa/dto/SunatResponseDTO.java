package org.istrfa.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * sunat response d t o
 */
@Getter
@Setter
public class SunatResponseDTO {

    private String code; //code
    private String message; //message
    private String digestValue; //digestValue
    private String fileName; //fileName

    private String ticket; //ticket

}
