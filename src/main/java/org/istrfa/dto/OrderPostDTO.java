package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class OrderPostDTO {

    private UUID id;
    private String firstname;
    private String lastname;
    private String phone;
    private String numberdocument;
    private String address;
    private UUID distritoId;
    private UUID clientId;


    private List<DetailOrderPostDTO> listdetails;


}
