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
@Table(name = "tbl_pers")
public class PersonEntity {

    @Id
    @Column(name = "tbl_pers_id_pk")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @GeneratedValue(generator = "UUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tbl_pers_type_document_fk", nullable = false)
    private MasterEntity typedocument; // Relación con la tabla tbl_data_mast (Tipo de documento)

    @Column(name = "tbl_pers_numdoc", nullable = false, length = 20)
    private String numerodoc; // Número de documento

    @Column(name = "tbl_pers_name", nullable = false, length = 250)
    private String name; // Nombre de la persona

    @Column(name = "tbl_pers_first_name", length = 250)
    private String firstname; // Primer apellido

    @Column(name = "tbl_pers_last_name", length = 250)
    private String lastname; // Segundo apellido

    @Column(name = "tbl_pers_addres", length = 300)
    private String address; // Dirección de la persona

    @Column(name = "tbl_pers_phone", length = 12)
    private String phone; // Teléfono de la persona


    @Column(name = "tbl_pers_date_create")
    private LocalDateTime datecreate = LocalDateTime.now(); // Fecha de creación

    public PersonEntity(UUID id) {
        this.id = id;
    }
}
