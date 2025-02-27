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
@Table(name = "tbl_invoice")
public class InvoiceEntity {

    @Id
    @Column(name = "tbl_invoice_id_pk")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2" )
    @GeneratedValue(generator = "UUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tbl_invoice_sale_fk")
    private SaleEntity sale;    // Venta asociada

    @Column(name = "tbl_invoice_date")
    private LocalDateTime invoicedate=LocalDateTime.now(); //Fecha de emision de Factura

    @Column(name = "tbl_invoice_totalamount")
    private String totalamount;     //Cantidad total de Factura

    @Column(name = "tbl_invoice_type")
    private String type;    //Tipo (Boleta o Factura)

    public InvoiceEntity(UUID id) {this.id = id; }
}
