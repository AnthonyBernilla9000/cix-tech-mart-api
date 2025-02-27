package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ProvinceDashboardDTO {

    private UUID id;
    private String name;
    private String longitud;
    private String latitud;

}
