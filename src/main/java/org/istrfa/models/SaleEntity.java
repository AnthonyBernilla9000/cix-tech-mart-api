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
@Table(name = "tbl_sale")
public class SaleEntity {

    @Id
    @Column(name = "tbl_sale_id_pk")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2" )
    @GeneratedValue(generator = "UUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tbl_sale_client_fk")
    private ClientEntity client; // Cliente asociado

    @Column(name="tbl_sale_state")
    private String state;   // Estado de Venta

    @Column(name= "tbl_sale_order_date")
    private LocalDateTime orderdate=LocalDateTime.now(); //Fecha de venta

    @Column(name= "tbl_sale_payment_method")
    private String paymentmethod;  // Método de pago

    @Column(name= "tbl_sale_total_amount")
    private String totalamount; //Cantidad total de venta


    public SaleEntity(UUID id) { this.id = id; }
}
