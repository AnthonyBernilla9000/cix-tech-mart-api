package org.istrfa.repositories;

import org.istrfa.models.WorkerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<WorkerEntity, UUID> {

    @Query("SELECT usr FROM WorkerEntity usr WHERE usr.person.numerodoc = ?1 AND usr.active = 1 ")
    WorkerEntity findByNumdoc(String numerodoc);

    @Query("SELECT usr FROM WorkerEntity usr WHERE usr.person.numerodoc = ?1 AND " +
            "usr.id != ?2 AND usr.active = 1 ")
    WorkerEntity findByNumdocEdit(String numerodoc, UUID idUser);


    @Query("SELECT usr FROM WorkerEntity usr WHERE usr.email = ?1 AND usr.active = 1 ")
    WorkerEntity findByEmail(String email);

    @Query("SELECT usr FROM WorkerEntity usr WHERE usr.email = ?1 AND " +
            "usr.id != ?2 AND usr.active = 1 ")
    WorkerEntity findByEmailEdit(String email, UUID idUser);
}
