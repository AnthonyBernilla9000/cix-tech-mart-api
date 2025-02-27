package org.istrfa.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tbl_detail_sale")
public class DetailSaleEntity {

    @Id
    @Column(name = "tbl_detailsale_id_pk")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2" )
    @GeneratedValue(generator = "UUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tbl_detailsale_sale_fk")
    private SaleEntity sale;    // Venta asociada

    @ManyToOne
    @JoinColumn(name = "tbl_detailsale_product_fk")
    private ProductEntity product;    // Producto asociado

    @Column(name = "tbl_detailsale_quantity")
    private String quantity;    // Cantidad de Venta

    @Column(name = "tbl_detailsale_price")
    private String price;   // Precio de Venta

    public DetailSaleEntity(UUID id) {this.id = id;}
}
