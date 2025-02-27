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
public class SaleDTO {

    private UUID id;    // Clave primaria
    private UUID clientId;  // Estado del Cliente
    private String state; // Estado de venta
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderdate; // Fecha de venta
    private String  paymentmethod; // Método de pago
    private String totalamount; // Cantidad total de venta

}
