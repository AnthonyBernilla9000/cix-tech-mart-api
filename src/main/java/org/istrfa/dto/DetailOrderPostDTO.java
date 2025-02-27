package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class DetailOrderPostDTO {

    private UUID productId;
    private Integer quantity;//Cantidad
    private Double unitprice;//Precio unitario
    private String description;
    private Integer type;//1=venta de producto; 2= otro

}
