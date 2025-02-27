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
public class OffersPostDTO {
    private UUID id; // Clave primaria
    private UUID productId; // Clave foránea del producto
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datestart; // Fecha de inicio de la oferta
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateend; // Fecha fin de la oferta
    private Double discount; // Campo activo o inactivo (int2 mapeado como Short)
    private String reason; // Motivo de la oferta



}
