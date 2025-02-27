package org.istrfa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * response d t o
 *
 * @param <T> the type parameter
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // para que no muestra variables nulas del DTO
public class ResponseDTO<T> {

    private Integer code; //code
    private String message; //message
    private T data; //data
}
