package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SessionStripeDTO {

    private String id; //Id de la sesión
    private String paymentIntent;
    private Long amountTotal;//Monto total del pago
    private String currency;
    private String customerEmail;
    private String paymentStatus;
    private String cardholder; //Nombre del titular de la tarjeta

    // Información del método de pago
    private String paymentMethodType; //Metodo de pago
    private String cardBrand;
    private String cardLast4;//Ultimos 4 digitos de la tarjeta
    private Long cardExpMonth;
    private Long cardExpYear;


}
