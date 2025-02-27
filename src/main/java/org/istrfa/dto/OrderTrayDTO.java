package org.istrfa.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class OrderTrayDTO {


    private UUID id;
    private String code;//Código del orden
    private String personfullname; //Nombre de la persona ingresada en la orden
    private Double igv;
    private Double subtotal;
    private Double total;
    private UUID stateId;
    private String stateName;
    private UUID statepagoId; //Estado de pago de la orden
    private String statepagoName; //Estado de pago de la orden

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datecreate;


}
