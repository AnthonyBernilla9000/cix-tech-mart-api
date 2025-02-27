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
@Table(name = "tbl_distr")
public class DistritoEntity {

    @Id
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @GeneratedValue(generator = "UUIDGenerator")
    @Column(name = "tbl_distr_id_pk", nullable = false)
    private UUID id;

    @Column(name = "tbl_distr_name", nullable = false, length = 300)
    private String name;

    @Column(name = "tbl_distr_ubigeo", length = 6)
    private String ubigeo;

    @ManyToOne
    @JoinColumn(name = "tbl_distr_prov_fk", nullable = false)
    private ProvinciaEntity province;  // Relación con la tabla 'tbl_provin'

    @Column(name = "tbl_distr_date_create")
    private LocalDateTime datecreate = LocalDateTime.now();

    @Column(name = "tbl_distr_active")
    private Integer active = 1;

    public DistritoEntity(UUID id) {
        this.id = id;
    }


}
