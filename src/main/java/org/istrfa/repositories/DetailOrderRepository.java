package org.istrfa.repositories;


import org.istrfa.models.DetailOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DetailOrderRepository extends JpaRepository<DetailOrderEntity, UUID> {

    @Query("SELECT doe FROM DetailOrderEntity doe WHERE doe.order.id = ?1 AND doe.active = 1 ")
    List<DetailOrderEntity> findByOrder(UUID idOrder);

    @Query("SELECT count(doe) FROM DetailOrderEntity doe WHERE doe.order.id = ?1 AND doe.active = 1 ")
    Integer getTotalItemsfindByOrder(UUID idOrder);

    //Obtener ids de los productos que mas se encuentran en detalles
    @Query("SELECT d.product.id, COUNT(d.product.id) AS repetitions " +
            "FROM DetailOrderEntity d " +
            "GROUP BY d.product.id " +
            "ORDER BY repetitions DESC")
    Page<Object[]> findMostRepeatedProducts(Pageable pageable);


    @Query("SELECT d.product.id as productid, " +
            "d.product.stock as productstock, " +
            "d.product.imgurl as productimg, " +
            "d.product.name as producto, " +
            " d.product.datecreate as datecreateproduct, " +
            " d.unitprice as unitprice, " +
            " COUNT(d) as totalorders, " +
            " SUM(d.quantity) as quantity, " +
            " SUM(d.quantity * d.unitprice) as total " +
            "FROM DetailOrderEntity d " +
            "WHERE d.datecreate BETWEEN :startDate AND :endDate AND d.active = 1 " +
            "GROUP BY d.product.id, d.product.stock, d.product.imgurl, " +
            "         d.product.name, d.product.datecreate, d.unitprice " +
            "ORDER BY quantity DESC")
    Page<Tuple> findProductStatisticsByRangeDate(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT d.product.id as productid, " +
            "d.product.stock as productstock, " +
            "d.product.imgurl as productimg, " +
            "d.product.name as producto, " +
            " d.product.datecreate as datecreateproduct, " +
            " d.unitprice as unitprice, " +
            " COUNT(d) as totalorders, " +
            " SUM(d.quantity) as quantity, " +
            " SUM(d.quantity * d.unitprice) as total " +
            "FROM DetailOrderEntity d " +
            "WHERE d.active = 1 " +
            "GROUP BY d.product.id, d.product.stock, d.product.imgurl, " +
            "         d.product.name, d.product.datecreate, d.unitprice " +
            "ORDER BY quantity DESC")
    Page<Tuple> findProductStatistics(Pageable pageable);


    @Query("SELECT d.product.name as producto, " +
            " d.product.datecreate as datecreateproduct," +
            "d.unitprice as unitprice, " +
            " COUNT(d) as totalorders, " +
            " SUM(d.quantity) as quantity, " +
            " SUM(d.quantity * d.unitprice) as total " +
            "FROM DetailOrderEntity d " +
            "GROUP BY d.product.name, d.product.datecreate, d.unitprice " +
            "ORDER BY quantity DESC")
    List<Tuple> findProductStatistics();




}
