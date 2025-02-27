package org.istrfa.services;

import org.istrfa.dto.*;
import org.istrfa.repositories.ClientRepository;
import org.istrfa.repositories.DetailOrderRepository;
import org.istrfa.repositories.OrderRepository;
import org.istrfa.repositories.ProductRepository;
import org.istrfa.utils.Constantes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class DashboardService {

    private final ClientRepository clientRepository;

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final DetailOrderRepository detailOrderRepository;

    @Autowired
    public DashboardService(ClientRepository clientRepository, OrderRepository orderRepository,
                            ProductRepository productRepository, DetailOrderRepository detailOrderRepository) {
        this.clientRepository = clientRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.detailOrderRepository = detailOrderRepository;
    }

    public DashboardDTO getDataDashboard(LocalDateTime datestart, LocalDateTime dateend, Integer size) {
        DashboardDTO dto = new DashboardDTO();
        if (Objects.nonNull(datestart) && Objects.nonNull(dateend)) {//Lógica cuando si viene las fecha
            dto.setTotalclients(clientRepository.getTotalClientsByDateRange(datestart, dateend));
            dto.setTotalorders(orderRepository.getTotalOrdersByDateRange(datestart, dateend));
            //Obtenemos las ganancias solo de aquellos pedidos que fueron pagados, no por pagar ni reembolsado
            Double totalprofits = orderRepository.getTotalGananciasByDateRange(datestart, dateend, Constantes.ESTADO_PAGADO);
            if (Objects.isNull(totalprofits)) totalprofits = 0.0;
            dto.setTotalprofits(totalprofits);
            dto.setTotalproducts(productRepository.getTotalProductsByDateRange(datestart, dateend));
        } else {
            dto.setTotalclients(clientRepository.getTotalClients());
            dto.setTotalorders(orderRepository.getTotalOrders());
            //Obtenemos las ganancias solo de aquellos pedidos que fueron pagados, no por pagar ni reembolsado
            Double totalprofits = orderRepository.getTotalGanancias(Constantes.ESTADO_PAGADO);
            if (Objects.isNull(totalprofits)) totalprofits = 0.0;
            dto.setTotalprofits(totalprofits);
            dto.setTotalproducts(productRepository.getTotalProducts());
        }
        //En cada metodo ya valido en caso las fechas vengan null
        dto.setListproducts(getProductsDashboard(datestart, dateend, size));
        dto.setListprovinces(getProvincesDashboard(datestart, dateend, size));
        dto.setListclients(getClientsDashboard(datestart, dateend, size));
        dto.setListdetailsmonth(getDetailxMonthDashboard(datestart, dateend, size));

        return dto;
    }

    private List<ProductDashboardDTO> getProductsDashboard(LocalDateTime datestart, LocalDateTime dateend, Integer size) {
        Pageable pageable = PageRequest.of(0, size); // Limitar a los 5 primeros registros

        Page<Tuple> pageResult;
        if (Objects.nonNull(datestart) && Objects.nonNull(dateend)) {
            pageResult = detailOrderRepository.findProductStatisticsByRangeDate(datestart, dateend, pageable);
        } else {
            pageResult = detailOrderRepository.findProductStatistics(pageable);
        }

        // Convertir el resultado en una lista de ProductDashboardDTO
        return pageResult.stream().map(tuple -> {
            ProductDashboardDTO dto = new ProductDashboardDTO();
            dto.setId(tuple.get("productid", UUID.class)); // Alias "productid"
            dto.setImgurl(tuple.get("productimg", String.class)); // Alias "productimg"
            dto.setStock(tuple.get("productstock", Integer.class)); // Alias "productstock"
            dto.setName(tuple.get("producto", String.class)); // Alias "producto"
            dto.setDatecreate(tuple.get("datecreateproduct", LocalDateTime.class)); // Alias "datecreateproduct"
            dto.setPrice(tuple.get("unitprice", Double.class)); // Alias "unitprice"
            dto.setTotalorders(tuple.get("totalorders", Long.class)); // Alias "totalorders"
            dto.setQuantity(tuple.get("quantity", Long.class)); // Alias "quantity"
            dto.setTotal(tuple.get("total", Double.class)); // Alias "total"
            return dto;
        }).collect(Collectors.toList());
    }

    private List<ProvinceDashboardDTO> getProvincesDashboard(LocalDateTime datestart, LocalDateTime dateend, Integer size) {
        Pageable pageable = PageRequest.of(0, size); // Limitar a los 5 primeros registros
        Page<Tuple> pageResult ;

        if (Objects.nonNull(datestart) && Objects.nonNull(dateend)) {
            pageResult = orderRepository.findProvinceStatisticsByRangeDate(datestart, dateend, pageable);
        } else {
            pageResult = orderRepository.findProvinceStatistics(pageable);
        }

        // Convertir el resultado en una lista de ProvinceDashboardDTO
        return pageResult.stream().map(tuple -> {
            ProvinceDashboardDTO dto = new ProvinceDashboardDTO();
            dto.setId(tuple.get("provinceid", UUID.class)); // Alias "producto"
            dto.setName(tuple.get("provincename", String.class)); // Alias "producto"
            dto.setLongitud(tuple.get("provincelongitud", String.class)); // Alias "producto"
            dto.setLatitud(tuple.get("provincelatitud", String.class)); // Alias "producto"
            return dto;
        }).collect(Collectors.toList());
    }

    private List<ClientDashboardDTO> getClientsDashboard(LocalDateTime datestart, LocalDateTime dateend, Integer size) {
        Pageable pageable = PageRequest.of(0, size); // Limitar a los 5 primeros registros
        Page<Tuple> pageResult;

        if (Objects.nonNull(datestart) && Objects.nonNull(dateend)) {
            pageResult = orderRepository.findClientStatisticsByRangeDate(datestart, dateend, pageable);
        } else {
            pageResult = orderRepository.findClientStatistics(pageable);
        }

        // Convertir el resultado en una lista de ClientDashboardDTO
        return pageResult.stream().map(tuple -> {
            ClientDashboardDTO dto = new ClientDashboardDTO();
            dto.setName(tuple.get("clientname", String.class)); // Alias "producto"
            dto.setPhoto(tuple.get("clientphoto", String.class)); // Alias "producto"
            dto.setEmail(tuple.get("clientemail", String.class)); // Alias "producto"
            dto.setQuantityorders(tuple.get("quantitypedidos", Long.class)); // Alias "producto"
            dto.setTotalorders(tuple.get("totalorders", Double.class)); // Alias "producto"
            return dto;
        }).collect(Collectors.toList());
    }

    private List<DetailxMonthDashboardDTO> getDetailxMonthDashboard(LocalDateTime datestart, LocalDateTime dateend, Integer size) {
        Pageable pageable = PageRequest.of(0, size); // Limitar a los 5 primeros registros
        Page<Tuple> pageResult;

        if (Objects.nonNull(datestart) && Objects.nonNull(dateend)) {
            pageResult = orderRepository.findOrdersAndRevenueByMonthByDateRange(datestart, dateend, Constantes.ESTADO_REEMBOLSADO, pageable);
        } else {
            pageResult = orderRepository.findOrdersAndRevenueByMonth(Constantes.ESTADO_REEMBOLSADO, pageable);
        }

        // Convertir el resultado en una lista de DetailxMonthDashboardDTO
        return pageResult.stream().map(tuple -> {
            DetailxMonthDashboardDTO dto = new DetailxMonthDashboardDTO();
            dto.setMonth(tuple.get("month", Integer.class)); // Alias "producto"
            dto.setYear(tuple.get("year", Integer.class)); // Alias "producto"
            dto.setQuantityorders(tuple.get("totalorders", Long.class)); // Alias "producto"
            dto.setTotalprofits(tuple.get("sumtotalorders", Double.class)); // Alias "producto"
            dto.setTotalreturned(tuple.get("totalreturns", Long.class)); // Alias "producto"
            return dto;
        }).collect(Collectors.toList());
    }


}
