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
@Table(name = "tbl_order")
public class OrderEntity {
    @Id
    @Column(name = "tbl_order_id_pk")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2" )
    @GeneratedValue(generator = "UUIDGenerator")
    private UUID id;

    @Column(name="tbl_order_first_name")
    private String firstname;

    @Column(name="tbl_order_last_name")
    private String lastname;

    @Column(name="tbl_order_company_name")
    private String companyname;

    @Column(name = "tbl_order_phone")
    private String phone;

    @Column(name = "tbl_order_email")
    private String email;

    @Column(name = "tbl_order_address")
    private String address;

    @Column(name = "tbl_order_subtotal")
    private Double subtotal;

    @Column(name = "tbl_order_igv")
    private Double igv;

    @Column(name = "tbl_order_total")
    private Double total;

    @ManyToOne
    @JoinColumn(name = "tbl_order_client_fk")
    private ClientEntity client;

    @ManyToOne
    @JoinColumn(name = "tbl_order_distr_fk")
    private DistritoEntity distrito;

    @Column(name = "tbl_order_active")
    private Integer active;   //Campo activo o inactivo

    @Column(name = "tbl_order_date_create")
    private LocalDateTime datecreate;

    @Column(name = "tbl_order_code")
    private String code;//Código del orden

    @Column(name = "tbl_order_num_code")
    private Integer numcode;//Número de código

    @ManyToOne
    @JoinColumn(name = "tbl_order_state_fk")
    private MasterEntity state; //Estado de la orden

    @Column(name = "tbl_order_date_preparation")
    private LocalDateTime datepreparation;

    @Column(name = "tbl_order_date_envio")
    private LocalDateTime dateenvio; // Fecha en la que se envia el pedido

    @Column(name = "tbl_order_date_delivery")
    private LocalDateTime datedelivery; //Fecha de entrega

    @ManyToOne
    @JoinColumn(name = "tbl_order_state_pago_fk")
    private MasterEntity statepago; //Estado de la orden

    @Column(name = "tbl_order_date_pago")
    private LocalDateTime datepay;//Fecha en la que se realizó el pago

    @Column(name = "tbl_order_session_stripe")
    private String idsessionstripe;//Id de la sesión en stripe cuando se pagó

    @Column(name = "tbl_order_pagoid_stripe")
    private String idpagostripe;//Id del pago en stripe cuando se pagó

    @OneToMany(mappedBy = "order")
    private List<DetailOrderEntity> detalles;

    public OrderEntity(UUID id) {this.id = id; }
}
