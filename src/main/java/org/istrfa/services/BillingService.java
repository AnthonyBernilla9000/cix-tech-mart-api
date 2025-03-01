package org.istrfa.services;

import lombok.extern.slf4j.Slf4j;
import org.istrfa.dto.DetalleOrdenPagoDTO;
import org.istrfa.dto.FacturacionDetalleDTO;
import org.istrfa.dto.SunatResponseDTO;
import org.istrfa.models.DetailOrderEntity;
import org.istrfa.models.OrderEntity;
import org.istrfa.sunat.LiFacManager;
import org.istrfa.utils.Constantes;
import org.istrfa.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class BillingService {

    private final LiFacManager liFacManager;

    @Autowired
    public BillingService(LiFacManager liFacManager) {
        this.liFacManager = liFacManager;
    }

    public SunatResponseDTO sendSunat(OrderEntity order, List<DetailOrderEntity> listdetail){
        FacturacionDetalleDTO dto=buildBillingDTO(order,listdetail);
        return liFacManager.createInvoice(dto);
    }

    private FacturacionDetalleDTO buildBillingDTO(OrderEntity order, List<DetailOrderEntity> listdetail){
        FacturacionDetalleDTO dto=new FacturacionDetalleDTO();
        dto.setId(order.getId());
        dto.setVoucherTypeCode("03");
        dto.setNumerodocumento(order.getNumberdocument());
        dto.setNombreusuario(order.getFirstname()+" "+order.getLastname());
        dto.setDireccion(order.getAddress());
        dto.setTotal(order.getTotal());
        dto.setSubTotal(order.getSubtotal());
        dto.setIgv(order.getIgv());
        dto.setSerie(Constantes.SERIE_BOLETA);
        dto.setNumerocomprobante(Util.completeWithZero(order.getNumcode(),8));//Tiene que ir en formato 00012345
        //Construimos la informnación de los detalles
        List<DetalleOrdenPagoDTO> list=new ArrayList<>();
        for(DetailOrderEntity x : listdetail){
            DetalleOrdenPagoDTO detaildto=new DetalleOrdenPagoDTO();
            detaildto.setCantidad(x.getQuantity());
            detaildto.setPrecio(x.getUnitprice());
            detaildto.setSubtotal(x.getSubtotal());
            detaildto.setIgv(x.getIgv());
            detaildto.setPorcentajeigv(Constantes.IGV);
            detaildto.setNametramite(Constantes.NAME_TRAMITE);
            list.add(detaildto);
        }
        dto.setItems(list);
        return dto;
    }


}
