package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class DetailOrderDTO {

    private UUID id;
    private String quantity;//Cantidad
    private String total;

    //Información del producto
    private UUID productId;
    private String productName;
    private String productImgurl;   //Url de la imagen
    private String productTypeName;   //Tipo de productos
    private String productMarcaName;   //Marca de productos
    private String productModeloName;   //Modelo de productos
    private Double productPrice;   //Precio del producto
    private String description;



}
