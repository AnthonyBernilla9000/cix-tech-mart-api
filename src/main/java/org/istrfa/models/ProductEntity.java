package org.istrfa.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tbl_product")
public class ProductEntity {

    @Id
    @Column(name = "tbl_product_id_pk")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2" )
    @GeneratedValue(generator = "UUIDGenerator")
    private UUID id;

    @Column(name = "tbl_product_name")
    private String name;    //Nombre del producto

    @Column(name = "tbl_product_description")
    private String description;     //Descripcion de producto

    @Column(name = "tbl_product_price")
    private Double price;   //Precio del producto

    @Column(name = "tbl_product_stock")
    private Integer stock;   //Stock de productos

    @Column(name = "tbl_product_img_url")
    private String imgurl;   //Url de la imagen

    @Column(name = "tbl_product_sku")
    private Integer sku;   //Sku de productos

    @Column(name = "tbl_product_code")
    private String code;   //Código unico de producto (tipo + sku)

    @ManyToOne
    @JoinColumn(name = "tbl_product_type_fk")
    private TypeMarcaModeloEntity type;   //Tipo de productos

    @ManyToOne
    @JoinColumn(name = "tbl_product_marc_fk")
    private TypeMarcaModeloEntity marca;   //Marca de productos

    @ManyToOne
    @JoinColumn(name = "tbl_product_modl_fk")
    private TypeMarcaModeloEntity modelo;   //Modelo de productos

    @Column(name = "tbl_product_active")
    private Integer active;   //Campo activo o inactivo

    @Column(name = "tbl_product_date_create")
    private LocalDateTime datecreate;

    @ManyToOne
    @JoinColumn(name = "tbl_product_status_fk")
    private MasterEntity status;   //Estado de producto

    @Column(name = "tbl_product_date_publication")
    private LocalDateTime datepublication;

    @OneToMany(mappedBy="product")
    private List<ImgnsxProductEntity> images;

    @OneToMany(mappedBy="product")
    private List<DescripAditPrdctEntity> descripaditionals;


    public ProductEntity(UUID id) {this.id = id; }
}
