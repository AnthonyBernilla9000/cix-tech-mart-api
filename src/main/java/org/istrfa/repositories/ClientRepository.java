package org.istrfa.repositories;

import org.istrfa.models.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

    @Query("SELECT usr FROM ClientEntity usr WHERE usr.email = ?1 AND usr.active = 1 ")
    ClientEntity findByEmail(String email);

    @Query("SELECT usr FROM ClientEntity usr WHERE usr.email= ?1 AND " +
            "usr.id != ?2 AND usr.active = 1 ")
    ClientEntity findByEmailEdit(String numerodoc, UUID idUser);

    @Query("SELECT count(c) FROM ClientEntity c WHERE c.datecreate BETWEEN :startDate AND :endDate AND c.active = 1 ")
    Integer getTotalClientsByDateRange(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT count(c) FROM ClientEntity c WHERE c.active = 1 ")
    Integer getTotalClients();



}
