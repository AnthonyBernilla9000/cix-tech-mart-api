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
@Table(name = "tbl_depart")
public class DepartamentoEntity {

    @Id
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @GeneratedValue(generator = "UUIDGenerator")
    @Column(name = "tbl_depart_id_pk", nullable = false)
    private UUID id;

    @Column(name = "tbl_depart_name", nullable = false, length = 300)
    private String name;

    @Column(name = "tbl_depart_ubigeo", length = 6)
    private String ubigeo;

    @Column(name = "tbl_depart_date_create", nullable = true)
    private LocalDateTime datecreate = LocalDateTime.now();

    @Column(name = "tbl_depart_active", nullable = true)
    private Integer active = 1;

}
