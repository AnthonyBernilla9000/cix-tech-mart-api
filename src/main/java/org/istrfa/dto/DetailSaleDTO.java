package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class DetailSaleDTO {
    private UUID id;    // Clave primaria
    private UUID saleId;    // Estado de Venta
    private UUID productId; // Estado de Producto
    private String quantity;    // Cantidad de venta
    private String price;   // Precio de Venta
}
