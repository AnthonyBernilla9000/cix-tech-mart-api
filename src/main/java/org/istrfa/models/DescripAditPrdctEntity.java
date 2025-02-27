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
@Table(name = "tbl_descrip_adit_prdct")
public class DescripAditPrdctEntity {

    @Id
    @Column(name = "tbl_descrip_adit_prdct_id_pk", nullable = false)
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2" )
    @GeneratedValue(generator = "UUIDGenerator")
    private UUID id;  // Clave primaria

    @Column(name = "tbl_descrip_adit_prdct_key", length = 200)
    private String key;  // Campo key

    @Column(name = "tbl_descrip_adit_prdct_value", length = 500)
    private String value;  // Campo value

    @ManyToOne
    @JoinColumn(name = "tbl_descrip_adit_prdct_product_fk")
    private ProductEntity product;  // Relación con la tabla productos (tbl_product)

    @Column(name = "tbl_descrip_adit_prdct_date_create")
    private LocalDateTime datecreate;  // Fecha de creación

    @Column(name = "tbl_descrip_adit_prdct_active")
    private Integer active;  // Campo activo o inactivo

}
