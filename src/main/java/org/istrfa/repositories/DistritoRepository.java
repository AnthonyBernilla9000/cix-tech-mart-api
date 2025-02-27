package org.istrfa.repositories;

import org.istrfa.models.DistritoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface DistritoRepository extends JpaRepository<DistritoEntity, UUID> {

    @Query("SELECT doe FROM DistritoEntity doe WHERE doe.province.id = ?1 AND doe.active = 1 ")
    List<DistritoEntity> findByProvince(UUID idProvince);

}
