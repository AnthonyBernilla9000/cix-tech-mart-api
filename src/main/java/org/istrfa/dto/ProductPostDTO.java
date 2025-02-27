package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ProductPostDTO {

    private UUID id;    //Clave primaria
    private String name;    //Nombre del producto
    private String description; //Descripción de producto
    private UUID statusId;   //Estado de producto
    private Double price;   //Precio del producto
    private Integer stock;   //Stock de productos
    private String imgurl;   //Url de la imagen
    private UUID typeId;   //Tipo de productos
    private UUID marcaId;   //Marca de productos
    private UUID modeloId;   //Modelo de productos

    List<ImgnsxProductDTO> listimages;//Listo de imagenes del producto
    List<DescripAditPrdctDTO> listdescripaditionals;//Lista de descripciones adicionales del producto

}
