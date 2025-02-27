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
@Table(name = "tbl_tp_mrc_mdl")
public class TypeMarcaModeloEntity {

    @Id
    @Column(name = "tbl_tp_mrc_mdl_id_pk", nullable = false)
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2" )
    @GeneratedValue(generator = "UUIDGenerator")
    private UUID id;

    @Column(name = "tbl_tp_mrc_mdl_name", length = 300, nullable = false)
    private String name;

    @Column(name = "tbl_tp_mrc_mdl_descp", length = 500)
    private String description;

    @Column(name = "tbl_tp_mrc_mdl_date_create")
    private LocalDateTime datecreate;

    @Column(name = "tbl_tp_mrc_mdl_id_parent")
    private UUID parentid;

    @Column(name = "tbl_tp_mrc_mdl_type", nullable = false)
    private Integer typeregist;//1=modelo;2=marca;3=tipo

    @Column(name = "tbl_tp_mrc_mdl_active")
    private Integer active;

    @Column(name = "tbl_tp_mrc_mdl_img_refer")
    private String image;

}
