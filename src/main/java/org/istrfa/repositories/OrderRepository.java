package org.istrfa.repositories;

import org.istrfa.models.OrderEntity;
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
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    @Query("SELECT max(dt.numcode) FROM OrderEntity dt WHERE dt.active = 1 ")
    Integer getMaxNumCode();

    @Query("SELECT count(o) FROM OrderEntity o WHERE o.datecreate BETWEEN :startDate AND :endDate AND o.active = 1 ")
    Integer getTotalOrdersByDateRange(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT count(o) FROM OrderEntity o WHERE o.active = 1 ")
    Integer getTotalOrders();

    @Query("SELECT SUM(o.total) FROM OrderEntity o WHERE o.datecreate BETWEEN :startDate AND :endDate " +
            "AND o.statepago.id = :statepago AND o.active = 1 ")
    Double getTotalGananciasByDateRange(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate, @Param("statepago") UUID estadopago);

    @Query("SELECT SUM(o.total) FROM OrderEntity o WHERE o.statepago.id = :statepago AND o.active = 1 ")
    Double getTotalGanancias(@Param("statepago") UUID estadopago);

    @Query("SELECT o FROM OrderEntity o WHERE o.client.id = :clientid " +
            "AND o.active = 1 ORDER BY o.datecreate DESC ")
    List<OrderEntity> getByClientId(@Param("clientid") UUID clientid);

    @Query("SELECT o.distrito.province.id as provinceid, " +
            " o.distrito.province.name as provincename, " +
            " o.distrito.province.latitud as provincelatitud, " +
            " o.distrito.province.longitud as provincelongitud, " +
            " COUNT(o) as quantitypedidos " +
            "FROM OrderEntity o " +
            "WHERE o.datecreate BETWEEN :startDate AND :endDate AND o.active = 1 " +
            "GROUP BY o.distrito.province.id, o.distrito.province.name, o.distrito.province.latitud, o.distrito.province.longitud " +
            "ORDER BY quantitypedidos DESC")
    Page<Tuple> findProvinceStatisticsByRangeDate(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT o.distrito.province.id as provinceid, " +
            " o.distrito.province.name as provincename, " +
            " o.distrito.province.latitud as provincelatitud, " +
            " o.distrito.province.longitud as provincelongitud, " +
            " COUNT(o) as quantitypedidos " +
            "FROM OrderEntity o " +
            "WHERE o.active = 1 " +
            "GROUP BY o.distrito.province.id, o.distrito.province.name, o.distrito.province.latitud, o.distrito.province.longitud " +
            "ORDER BY quantitypedidos DESC")
    Page<Tuple> findProvinceStatistics(Pageable pageable);

    @Query("SELECT o.client.fullname as clientname, " +
            " o.client.email as clientemail, " +
            " o.client.photo as clientphoto, " +
            " COUNT(o) as quantitypedidos, " +
            " SUM(o.total) as totalorders " +
            "FROM OrderEntity o " +
            "WHERE o.datecreate BETWEEN :startDate AND :endDate AND o.active = 1 " +
            "GROUP BY o.client.fullname, o.client.email, o.client.photo " +
            "ORDER BY quantitypedidos DESC")
    Page<Tuple> findClientStatisticsByRangeDate(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT o.client.fullname as clientname, " +
            " o.client.email as clientemail, " +
            " o.client.photo as clientphoto, " +
            " COUNT(o) as quantitypedidos, " +
            " SUM(o.total) as totalorders " +
            "FROM OrderEntity o " +
            "WHERE o.active = 1 " +
            "GROUP BY o.client.fullname, o.client.email, o.client.photo " +
            "ORDER BY quantitypedidos DESC")
    Page<Tuple> findClientStatistics(Pageable pageable);

    @Query("SELECT MONTH(o.datecreate) as month, " +
            " YEAR(o.datecreate) as year, " +
            " COUNT(o) as totalorders, " +
            " SUM(o.total) as sumtotalorders, " +
            " COUNT(CASE WHEN o.statepago.id = :stateReturn THEN 1 END) as totalreturns " +
            "FROM OrderEntity o " +
            "WHERE o.datecreate BETWEEN :startDate AND :endDate AND o.active = 1 " +
            "GROUP BY YEAR(o.datecreate), MONTH(o.datecreate) " +
            "ORDER BY year ASC, month ASC")
    Page<Tuple> findOrdersAndRevenueByMonthByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("stateReturn") UUID stateReturn,
            Pageable pageable
    );

    @Query("SELECT MONTH(o.datecreate) as month, " +
            " YEAR(o.datecreate) as year, " +
            " COUNT(o) as totalorders, " +
            " SUM(o.total) as sumtotalorders, " +
            " COUNT(CASE WHEN o.statepago.id = :stateReturn THEN 1 END) as totalreturns " +
            "FROM OrderEntity o " +
            "WHERE o.active = 1 " +
            "GROUP BY YEAR(o.datecreate), MONTH(o.datecreate) " +
            "ORDER BY year ASC, month ASC")
    Page<Tuple> findOrdersAndRevenueByMonth(
            @Param("stateReturn") UUID stateReturn,
            Pageable pageable
    );

}
