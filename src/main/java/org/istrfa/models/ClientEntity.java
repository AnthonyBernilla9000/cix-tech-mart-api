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
@Table(name = "tbl_client")
public class  ClientEntity {

    @Id
    @Column(name = "tbl_client_id_pk")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @GeneratedValue(generator = "UUIDGenerator")
    private UUID id; // Clave primaria (tipo varchar)

//    @ManyToOne
//    @JoinColumn(name = "tbl_client_pers_fk")
//    private PersonEntity person; // Persona asociada

    @ManyToOne
    @JoinColumn(name = "tbl_client_state_fk")
    private MasterEntity state; // Estado de la persona

    @Column(name = "tbl_client_name")
    private String name; // Name del cliente (se actualiza con el nombre colocado en la orden)

    @Column(name = "tbl_client_lastname")
    private String lastname; // Apellidos del cliente (se actualiza con los apellidos colocado en la orden)

    @Column(name = "tbl_client_fullname")
    private String fullname; // Nombre completo del cliente

    @Column(name = "tbl_client_email")
    private String email; // Email del cliente

    @Column(name = "tbl_client_photo")
    private String photo; // Photo del cliente

    @Column(name = "tbl_client_id_firebase")
    private String idfirebase; // Id del cliente en firebase

    @Column(name = "tbl_client_date_create")
    private LocalDateTime datecreate=LocalDateTime.now(); // Fecha de creación

    @Column(name = "tbl_client_active")
    private Integer active=1; // Campo activo o inactivo (1 o 0)

    @ManyToOne
    @JoinColumn(name = "tbl_client_dist_fk")
    private DistritoEntity district; //Distrito del cliente (se actualiza con el distriti colocado en la orden)

    @Column(name = "tbl_client_address")
    private String address; // Dirección del cliente (se actualiza con la dirección colocado en la orden)
    @Column(name = "tbl_client_phone")
    private String phone; // Dirección del cliente (se actualiza con el telefono colocado en la orden)

    // Constructor con el id, útil para cuando se necesita solo el ID
    public ClientEntity(UUID id) {
        this.id = id;
    }

}
