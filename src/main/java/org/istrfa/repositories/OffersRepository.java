package org.istrfa.repositories;

import org.istrfa.models.OffersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OffersRepository extends JpaRepository<OffersEntity, UUID> {

    @Query("SELECT o FROM OffersEntity o " +
            "WHERE o.dateend >= :fechaInicio AND o.datestart <= :fechaFin AND o.product.id = :product AND o.active = 1 ")
    OffersEntity findOffersExist(@Param("fechaInicio") LocalDateTime fechaInicio,
                                 @Param("fechaFin") LocalDateTime fechaFin, @Param("product") UUID product);

    @Query("SELECT o FROM OffersEntity o " +
            "WHERE o.dateend >= :fechaInicio AND o.datestart <= :fechaFin AND o.product.id = :product " +
            "AND o.id != :idregistro AND o.active = 1 ")
    OffersEntity findOffersExistEdit(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin,
                                     @Param("product") UUID product, @Param("idregistro") UUID idregistro);

    @Query("SELECT o FROM OffersEntity o " +
            "WHERE :fecha BETWEEN o.datestart AND o.dateend AND o.status.id = :status AND o.active = 1")
    List<OffersEntity> findOffersByDate(@Param("fecha") LocalDateTime fecha, @Param("status") UUID status);

    @Query("SELECT o.product.id as productid, o.discount as discount FROM OffersEntity o " +
            "WHERE :fecha BETWEEN o.datestart AND o.dateend AND o.status.id = :status AND o.active = 1 ")
    List<Tuple> findOffersByDateTuple(@Param("fecha") LocalDateTime fecha, @Param("status") UUID status);


}
