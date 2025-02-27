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
@Table(name = "tbl_provin")
public class ProvinciaEntity {

    @Id
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @GeneratedValue(generator = "UUIDGenerator")
    @Column(name = "tbl_provin_id_pk", nullable = false)
    private UUID id;

    @Column(name = "tbl_provin_name", nullable = false, length = 300)
    private String name;

    @Column(name = "tbl_provin_ubigeo", length = 6)
    private String ubigeo;

    @ManyToOne
    @JoinColumn(name = "tbl_provin_depr_fk", nullable = false)
    private DepartamentoEntity department;  // Relación con la tabla 'tbl_depart'

    @Column(name = "tbl_provin_long", length = 150)
    private String longitud;

    @Column(name = "tbl_provin_lati", length = 150)
    private String latitud;

    @Column(name = "tbl_provin_date_create")
    private LocalDateTime datecreate = LocalDateTime.now();

    @Column(name = "tbl_provin_active", nullable = true)
    private Integer active = 1;


}
