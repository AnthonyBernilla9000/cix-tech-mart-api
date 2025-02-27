package org.istrfa.repositories;

import org.istrfa.models.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    @Query("SELECT p FROM ProductEntity p WHERE LOWER(p.name) = LOWER(?1) " +
            "AND p.modelo.id = ?2  AND p.active = 1 ")
    ProductEntity findByNameAndModelo(String nombreproceso, UUID idmodelo);

    @Query("SELECT p FROM ProductEntity p WHERE LOWER(p.name) = LOWER(?1) " +
            "AND p.modelo.id = ?2  AND p.id != ?3 AND p.active = 1 ")
    ProductEntity findByNameAndModeloEdit(String nombreproceso, UUID idmodelo, UUID idRegistro);

    @Query("SELECT p FROM ProductEntity p WHERE p.id IN ?1 ")
    List<ProductEntity> findProductsByIds(List<UUID> ids);

    @Query("SELECT count(p) FROM ProductEntity p WHERE p.datecreate BETWEEN :startDate AND :endDate AND p.active = 1 ")
    Integer getTotalProductsByDateRange(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT count(p) FROM ProductEntity p WHERE p.active = 1 ")
    Integer getTotalProducts();

    @Query("SELECT max(p.sku) FROM ProductEntity p WHERE p.type.id = ?1 AND p.active = 1 ")
    Integer getMaxNumSku(UUID idType);


}
