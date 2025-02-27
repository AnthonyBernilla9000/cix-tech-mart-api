package org.istrfa.repositories;

import org.istrfa.models.MasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MasterRepository extends JpaRepository<MasterEntity, UUID> {

    @Query("SELECT dt FROM MasterEntity dt WHERE dt.prefix = ?1 " +
            "AND dt.correlative!=0 AND dt.active = 1 ORDER BY dt.correlative ASC")
    List<MasterEntity> findByPrefix(Integer prefix);

    @Query("SELECT t FROM MasterEntity t WHERE t.prefix = ?1 AND " +
            "t.correlative IN (?2) AND t.active=1" +
            "ORDER BY t.correlative ASC")
    List<MasterEntity> findByPrefixAndCorrelatives(Integer prefix, Integer[] correlatives);

}
