package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RefundPostDTO {

    private UUID orderId; //Id de la orden
    private UUID reasonId; //Motivo del reembolso
    private String detail; //Detalle del reembolso


}
