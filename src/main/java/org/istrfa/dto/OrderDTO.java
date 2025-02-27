package org.istrfa.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class OrderDTO {

    private UUID id;
    private String firstname;
    private String lastname;
    private String personfullname; //Nombre de la persona ingresada en la orden

    private String phone;
    private String address;
    private Double subtotal;
    private Double igv;
    private Double total;

    private String distritoName;
    private String distritoProvinceName;
    private String distritoProvinceDepartmentName;

    private String code;//Código del orden
    private Integer numcode;//Número de código
    private UUID stateId;
    private String stateName;
    private UUID statepagoId; //Estado de pago de la orden

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datepreparation;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateenvio;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datedelivery;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datecreate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datepay;


    private List<DetailOrderDTO> listdetails;


    //Propiedades del pago
    private String codetransaction; //Código de la trasaccion(parte del id de la transaccion)
    private String paymentmethod; //Metodo de pago
    private String cardholder; //Nombre del titular de la tarjeta
    private String numbercard; //Número de la tarjeta oculato: XXXX XXXX XXXX 4242
    private Double totalmount;//Monto total del pago

    //Información del cliente
    private String clientEmail;//Email del cliente logeado
    private String clientPhoto;//Foto del cliente logeado
    private String clientFullname;//Nombre completo del cliente logeado

}
