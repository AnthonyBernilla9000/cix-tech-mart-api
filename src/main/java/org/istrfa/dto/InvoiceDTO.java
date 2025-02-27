package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class InvoiceDTO {
    private UUID id;    // Clave primaria
    private UUID saleId;    // Estado de Venta
    private LocalDateTime invoicedate;  //Fecha de emision de Factura
    private String totalamount;    //Monto total de Factura
    private String type; //Tipo (Boleta o Factura)
}
