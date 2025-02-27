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
public class ProductDashboardDTO {

    private UUID id;
    private String name;
    private String imgurl;
    private Integer stock;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datecreate;
    private Double price;
    private Long totalorders;
    private Long quantity;
    private Double total;

}
