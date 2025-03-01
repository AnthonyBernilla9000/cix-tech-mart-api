package org.istrfa.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class FacturacionDetalleDTO {

    private UUID id; //id
    private String voucherTypeCode; //voucherTypeCode
    private String numerodocumento; //numerodocumento
    private String nombreusuario; //nombreusuario
    private String direccion; //direccion
    private List<DetalleOrdenPagoDTO> items; //items
    private Double total; //total
    private Double subTotal; //subTotal
    private Double igv; //igv
    private String serie; //serie
    private String numerocomprobante; //numerocomprobante


}
