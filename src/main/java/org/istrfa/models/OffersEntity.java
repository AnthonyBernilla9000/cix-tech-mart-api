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
@Table(name = "tbl_offers")
public class OffersEntity {

    @Id
    @Column(name = "tbl_offers_id_pk")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @GeneratedValue(generator = "UUIDGenerator")
    private UUID id; // Clave primaria

    @ManyToOne
    @JoinColumn(name = "tbl_offers_product_fk", nullable = false)
    private ProductEntity product; // Clave foránea del producto

    @Column(name = "tbl_offers_date_start", nullable = false)
    private LocalDateTime datestart; // Fecha de inicio de la oferta

    @Column(name = "tbl_offers_date_end", nullable = false)
    private LocalDateTime dateend; // Fecha fin de la oferta

    @Column(name = "tbl_offers_date_create")
    private LocalDateTime datecreate; // Fecha de creación

    @Column(name = "tbl_offers_active")
    private Integer active; // Campo activo o inactivo (int2 mapeado como Short)

    @ManyToOne
    @JoinColumn(name = "tbl_offers_status_fk")
    private MasterEntity status; // Estado de la oferta

    @Column(name = "tbl_offers_discount")
    private Double discount; // Campo activo o inactivo (int2 mapeado como Short)

    @Column(name = "tbl_offers_descr")
    private String reason; // Motivo de la oferta

    // Constructor adicional
    public OffersEntity(UUID id) {
        this.id = id;
    }



}
