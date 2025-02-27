package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class OrderFilterDTO {

    private String code;//Código del orden
    private String personfullname; //Nombre de la persona ingresada en la orden
    private UUID stateId;//Estado de la orden
    private UUID statepagoId;//Estado de pago


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datestart; //Fecha inicio de creación de la orden
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateend; //Fecha fin de creación de la orden


}
