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
@Table(name = "tbl_detail_order")
public class DetailOrderEntity {

    @Id
    @Column(name = "tbl_detail_order_id_pk")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2" )
    @GeneratedValue(generator = "UUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tbl_detail_order_product_fk")
    private ProductEntity product;

    @Column(name = "tbl_detail_order_quantity")
    private Integer quantity;//Cantidad

    @Column(name = "tbl_detail_order_total")
    private Double total;

    @ManyToOne
    @JoinColumn(name = "tbl_detail_order_ord_fk")
    private OrderEntity order;

    @Column(name = "tbl_detail_order_unit_price")
    private Double unitprice;

    @Column(name = "tbl_detail_order_active")
    private Integer active;   //Campo activo o inactivo

    @Column(name = "tbl_detail_order_date_create")
    private LocalDateTime datecreate;

    @Column(name = "tbl_detail_order_descript")
    private String description;

    @Column(name = "tbl_detail_order_type")
    private Integer type;//1=venta de producto; 2= otro

    ////////////////// No se utiliza el subtotal ni igv

    @Column(name = "tbl_detail_order_sub_total")
    private Double subtotal;

    @Column(name = "tbl_detail_order_igv")
    private Double igv;

    public DetailOrderEntity(UUID id) {this.id = id;}
}
