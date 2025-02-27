package org.istrfa.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FacturacionPostDTO {

    private UUID id; //id
    private String fileName; //fileName
    private SupplierEconomicZoneDTO supplier; //supplier
    private FacturacionDetalleDTO billingDetail; //billingDetail
    private String voucherTypeCode; //voucherTypeCode


    // Otros usos para valores dinámicos en una clase estática
    private String pathApacheOrgXml;

    private String userClaveSol;
    private String passClaveSol;
}
