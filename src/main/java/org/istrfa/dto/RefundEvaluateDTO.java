package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RefundEvaluateDTO {

    private UUID refundId;
    private UUID result;
    private String reply;
    private UUID workerId;


}
