package org.istrfa.repositories;

import org.istrfa.models.DescripAditPrdctEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DescripAditPrdctRepository extends JpaRepository<DescripAditPrdctEntity, UUID> {

    @Modifying
    @Query("DELETE DescripAditPrdctEntity i WHERE i.product.id = ?1 ")
    void deleteByProductId(UUID idProduct);

}
