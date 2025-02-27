package org.istrfa.repositories;

import org.istrfa.models.ProvinciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProvinciaRepository extends JpaRepository<ProvinciaEntity, UUID> {
    @Query("SELECT doe FROM ProvinciaEntity doe WHERE doe.department.id = ?1 AND doe.active = 1 ")
    List<ProvinciaEntity> findByDepart(UUID idDepart);

}
