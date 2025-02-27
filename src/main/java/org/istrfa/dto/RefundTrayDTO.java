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
public class RefundTrayDTO {

    private UUID id;
    private String orderCode; //Id de la orden
    private String reasonName; //Motivo del reembolso
    private String stateId; //Estado del reembolso
    private String stateName; //Estado del reembolso
    private String resultId; //Resultado del reembolso
    private String resultName; //Resultado del reembolso

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datecreate;


}
