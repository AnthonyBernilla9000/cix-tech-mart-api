package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DashboardDTO {

    private Integer totalclients;//Número total de clientes
    private Integer totalorders;//Número total de pedidos
    private Double totalprofits;//Total de ganancias
    private Integer totalproducts;//Número total de productos
    private List<ProductDashboardDTO> listproducts;//Lista de productos mas vendido
    private List<ProvinceDashboardDTO> listprovinces;//Lista de provincias donde mas se vende
    private List<ClientDashboardDTO> listclients;//Lista de clientes que mas pedidos han realizado
    private  List<DetailxMonthDashboardDTO> listdetailsmonth;//Detalles por mes
}
