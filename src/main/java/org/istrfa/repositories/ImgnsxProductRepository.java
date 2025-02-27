package org.istrfa.repositories;

import org.istrfa.models.ImgnsxProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ImgnsxProductRepository extends JpaRepository<ImgnsxProductEntity, UUID> {

    @Modifying
    @Query("DELETE ImgnsxProductEntity i WHERE i.product.id = ?1 ")
    void deleteByProductId(UUID idProduct);

}
