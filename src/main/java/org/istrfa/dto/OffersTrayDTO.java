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
public class OffersTrayDTO {
    private UUID id; // Clave primaria
    private UUID productId; // Clave foránea del producto
    private String productName; // Clave foránea del producto
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datestart; // Fecha de inicio de la oferta
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateend; // Fecha fin de la oferta
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datecreate; // Fecha de creación
    private Double discount; // Campo activo o inactivo (int2 mapeado como Short)
    private UUID statusId; // Estado de la oferta
    private String statusName; // Estado de la oferta


}
