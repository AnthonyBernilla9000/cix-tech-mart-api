package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClientDashboardDTO {
    private String name;
    private String photo;
    private String email;
    private Long quantityorders;
    private Double totalorders;

}
