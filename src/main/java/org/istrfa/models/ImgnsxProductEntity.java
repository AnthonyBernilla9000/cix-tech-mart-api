package org.istrfa.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tbl_imgs_prdct")
public class ImgnsxProductEntity {

    @Id
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2" )
    @GeneratedValue(generator = "UUIDGenerator")
    @Column(name = "tbl_imgs_prdct_id_pk", nullable = false)
    private UUID id;  // Clave primaria

    @ManyToOne
    @JoinColumn(name = "tbl_imgs_prdct_product_fk", nullable = false)
    private ProductEntity product;  // Relación con la tabla productos (tbl_product)

    @Column(name = "tbl_imgs_prdct_name_color", length = 250)
    private String namecolor;  // Nombre del color de la imagen

    @Column(name = "tbl_imgs_prdct_color", length = 100)
    private String codecolor;  // Código del color

    @Column(name = "tbl_imgs_prdct_url_img", nullable = false, length = 500)
    private String urlimg;  // URL donde se guardó la imagen

    @Column(name = "tbl_imgs_prdct_date_create")
    private LocalDateTime datecreate;  // Fecha de creación

    @Column(name = "tbl_imgs_prdct_active")
    private Integer active;  // Campo activo o inactivo

    
}
