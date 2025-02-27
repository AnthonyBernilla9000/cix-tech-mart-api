package org.istrfa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleOrdenPagoDTO {

    private Double subtotal; //subtotal
    private Double igv; //igv
//    private Double total; //total
    private Double porcentajeigv;

    @JsonProperty("monto")
    private Double precio; //precio unitario
    private Integer cantidad; //cantidad
    private String nametramite; //Tramite
}
