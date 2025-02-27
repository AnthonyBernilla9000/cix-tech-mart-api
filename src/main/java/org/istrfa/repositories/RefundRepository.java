package org.istrfa.repositories;


import org.istrfa.models.RefundEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefundRepository extends JpaRepository<RefundEntity, UUID> {

    @Query("SELECT r FROM RefundEntity r WHERE r.order.id = ?1 AND r.state.id = ?2 AND r.active = 1 ")
    RefundEntity findByOrderState(UUID idOrder,UUID idState);


}
