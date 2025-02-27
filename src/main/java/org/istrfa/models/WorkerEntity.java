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
@Table(name = "tbl_worker")
public class WorkerEntity {

    @Id
    @Column(name = "tbl_user_id_pk")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @GeneratedValue(generator = "UUIDGenerator")
    private UUID id; // Clave primaria

    @ManyToOne
    @JoinColumn(name = "tbl_user_pers_fk")
    private PersonEntity person; // Persona asociada

    @Column(name = "tbl_user_photo")
    private String photo; // Foto del trabajador

    @ManyToOne
    @JoinColumn(name = "tbl_user_state_fk")
    private MasterEntity state; // Estado del trabajador

    @ManyToOne
    @JoinColumn(name = "tbl_user_type_fk")
    private MasterEntity type; // Tipo de usuario

    @Column(name = "tbl_user_auth_id")
    private String authid; // Id del usuario en firebase

    @Column(name = "tbl_user_date_create")
    private LocalDateTime datecreate=LocalDateTime.now(); // Fecha de creación

    @Column(name = "tbl_user_active")
    private Integer active=1; // Campo activo o inactivo (1 o 0)

    @Column(name = "tbl_user_email")
    private String email; // Email del usuario

    // Constructor con el id, útil para cuando se necesita solo el ID
    public WorkerEntity(UUID id) {
        this.id = id;
    }

}
