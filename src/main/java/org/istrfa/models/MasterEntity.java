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
@Table(name = "tbl_data_mast")
public class MasterEntity {

    @Id
    @Column(name = "tbl_data_mast_id_pk")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @GeneratedValue(generator = "UUIDGenerator")
    private UUID id;

    @Column(name = "tbl_data_mast_prefix")
    private Integer prefix;


    @Column(name = "tbl_data_mast_correl")
    private Integer correlative;

    @Column(name = "tbl_data_mast_name")
    private String name;

    @Column(name = "tbl_data_mast_descrip")
    private String description;

    @Column(name = "tbl_data_mast_active")
    private Integer active;

    @Column(name = "tbl_data_mast_date_create")
    private LocalDateTime datecreation = LocalDateTime.now(); // Fecha de creación


    public MasterEntity(UUID id) {
        this.id = id;
    }

}
