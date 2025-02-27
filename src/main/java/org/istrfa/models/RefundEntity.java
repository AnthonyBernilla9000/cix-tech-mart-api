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
@Table(name = "tbl_refund")
public class RefundEntity {

    @Id
    @Column(name = "tbl_refund_id_pk")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2" )
    @GeneratedValue(generator = "UUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tbl_refund_order_fk")
    private OrderEntity order; //Id de la orden

    @ManyToOne
    @JoinColumn(name = "tbl_refund_reason_fk")
    private MasterEntity reason; //Motivo del reembolso

    @Column(name = "tbl_refund_datail")
    private String detail; //Detalle del reembolso


    @ManyToOne
    @JoinColumn(name = "tbl_refund_state_fk")
    private MasterEntity state; //Estado del reembolso

    @ManyToOne
    @JoinColumn(name = "tbl_refund_result_fk")
    private MasterEntity result; //Resultado del reembolso

    @Column(name = "tbl_refund_reply")
    private String reply; //Respuesta del reembolso

    @Column(name = "tbl_refund_date_review")
    private LocalDateTime datereview; //Fecha de revisión

    @ManyToOne
    @JoinColumn(name = "tbl_refund_worker_fk")
    private WorkerEntity worker; //Trabajador que realiza la revisión

    @Column(name = "tbl_refund_date_create")
    private LocalDateTime datecreate;

    @Column(name = "tbl_refund_active")
    private Integer active;   //Campo activo o inactivo


}
