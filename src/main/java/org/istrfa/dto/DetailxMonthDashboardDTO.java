package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetailxMonthDashboardDTO {
    private Integer month;
    private Integer year;
    private Long quantityorders; //Total de ordenes
    private Double totalprofits; //Total de ganancias
    private Long totalreturned; //Total de ordenes devueltas
}
