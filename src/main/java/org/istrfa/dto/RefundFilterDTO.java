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
public class RefundFilterDTO {

    private String orderCode; //Código de la orden
    private UUID stateId; //Estado del reembolso
    private UUID resultId; //Resultado del reembolso

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datestart; //Fecha inicio de creación de la solicitud
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateend; //Fecha fin de creación de la solicitud

}
