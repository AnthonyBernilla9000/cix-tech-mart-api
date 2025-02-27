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
public class ProductTrayDTO {


    private UUID id;    //Clave primaria
    private String name;    //Nombre del producto
    private String description; //Descripción de producto
    private Double price;   //Precio del producto
    private Integer stock;   //Stock de productos
    private String imgurl;   //Url de la imagen
    private UUID typeId;   //Tipo de productos
    private String typeName;   //Tipo de productos
    private UUID marcaId;   //Marca de productos
    private String marcaName;   //Marca de productos
    private UUID modeloId;   //Modelo de productos
    private String modeloName;   //Modelo de productos

    private UUID statusId;   //Estado de producto
    private String statusDescription;   //Estado de producto

    private Double discount;   //Descuento del producto en una oferta


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datecreate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datepublication;

}
