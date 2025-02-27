package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ProductFilterDTO {

    private String name;    //Nombre del producto
    private String type; //Tipo
    private String marca; //marca

    //Para ver si tiene stock o no
    private String status;//viene el texto En stock o Agotado

    //Para el rango de precios
    private Double pricemin;
    private Double pricemax;

    //Para el admin
    private UUID typeId; //Tipo
    private UUID marcaId; //marca



}
