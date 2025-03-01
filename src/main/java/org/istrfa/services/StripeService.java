package org.istrfa.services;

import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.ChargeListParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.istrfa.dto.ResponseDTO;
import org.istrfa.dto.SessionStripeDTO;
import org.istrfa.dto.SunatResponseDTO;
import org.istrfa.models.DetailOrderEntity;
import org.istrfa.models.MasterEntity;
import org.istrfa.models.OrderEntity;
import org.istrfa.models.ProductEntity;
import org.istrfa.repositories.DetailOrderRepository;
import org.istrfa.repositories.OrderRepository;
import org.istrfa.repositories.ProductRepository;
import org.istrfa.utils.Constantes;
import org.istrfa.utils.Util;
import org.istrfa.utils.UtilHtml;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class StripeService {

    // La clave secreta de Stripe se obtiene del archivo de configuración
    @Value("${stripe.key-secret}")
    private String stripeApiKey;


    private final OrderRepository orderRepository;
    private final DetailOrderRepository detailOrderRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;
    private final TicketService ticketService;
    private final BillingService billingService;

    @Value("${templates.order-pay}")
    private String htmlorderpay;


    @Autowired
    public StripeService(OrderRepository orderRepository, DetailOrderRepository detailOrderRepository, ProductRepository productRepository, EmailService emailService, TicketService ticketService, BillingService billingService) {
        this.orderRepository = orderRepository;
        this.detailOrderRepository = detailOrderRepository;
        this.productRepository = productRepository;
        this.emailService = emailService;
        this.ticketService = ticketService;
        this.billingService = billingService;
    }


    public ResponseDTO<String> createCheckoutSession(UUID idOrden, String urlreturn) throws StripeException {
        // Configurar Stripe con la clave secreta
        Stripe.apiKey = stripeApiKey;
        OrderEntity orden = orderRepository.getById(idOrden);

        // Construir los items del carrito basados en los detalles de la orden
        List<SessionCreateParams.LineItem> lineItems = orden.getDetalles().stream()
                .map(detail -> {
                    String productName = (detail.getType() == 1)
                            ? detail.getProduct().getName()
                            : detail.getDescription();

                    return SessionCreateParams.LineItem.builder()
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("pen") // Moneda
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(productName) // Usar el nombre o la descripción según el tipo
                                                            .build()
                                            )
                                            .setUnitAmount(Math.round(detail.getUnitprice() * 100)) // Precio en centavos
                                            .build()
                            )
                            .setQuantity(detail.getQuantity().longValue()) // Cantidad
                            .build();
                })
                .collect(Collectors.toList());


        // Crear la sesión de Stripe Checkout
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT) // Modo de pago único
                .setSuccessUrl(urlreturn + "/shop/order;session_payment_id={CHECKOUT_SESSION_ID}" + ";idOrder=" + orden.getId())
                .setCancelUrl(urlreturn + "/shop/order?idOrder=" + orden.getId())
                .addAllLineItem(lineItems) // Agregar todos los ítems
                .build();


        Session session = Session.create(params);

        // Retornar el ID de la sesión al cliente
        return new ResponseDTO<>(Constantes.HTTP_STATUS_CORRECT, "Sesión obtenida correctamente", session.getId());

    }

    public SessionStripeDTO getSessionDetails(String sessionId, UUID idOrden, Boolean updateState) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        SessionStripeDTO sessionDetails = new SessionStripeDTO();

        try {
            // Recuperar la sesión de Checkout
            Session session = Session.retrieve(sessionId);
            log.info("Sesión de Stripe recuperada > {}", session);

            // Mapear información básica de la sesión al DTO
            sessionDetails.setId(session.getId());
            sessionDetails.setPaymentIntent(session.getPaymentIntent());
            sessionDetails.setAmountTotal(session.getAmountTotal());
            sessionDetails.setCurrency(session.getCurrency());
            sessionDetails.setCustomerEmail(session.getCustomerDetails() != null ? session.getCustomerDetails().getEmail() : null);
            sessionDetails.setPaymentStatus(session.getPaymentStatus());


            String pagoId = null;
            // Obtener detalles del PaymentIntent
            String paymentIntentId = session.getPaymentIntent();
            if (paymentIntentId != null) {
                PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

                // Obtener el pago ID desde el PaymentIntent
                ChargeListParams params = ChargeListParams.builder()
                        .setPaymentIntent(paymentIntentId)
                        .build();

                List<Charge> charges = Charge.list(params).getData();
                if (!charges.isEmpty()) {
                    Charge charge = charges.get(0); // Tomar el primer charge
                    pagoId = charge.getId();
                }

                // Obtener el método de pago asociado
                String paymentMethodId = paymentIntent.getPaymentMethod();
                if (paymentMethodId != null) {

                    PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
                    log.info("Esta es la info del paymentmethod {}", paymentMethod);

                    // Mapear información del método de pago al DTO
                    sessionDetails.setPaymentMethodType(paymentMethod.getType());
                    if ("card".equals(paymentMethod.getType()) && paymentMethod.getCard() != null) {
                        sessionDetails.setCardBrand(paymentMethod.getCard().getBrand());
                        sessionDetails.setCardLast4(paymentMethod.getCard().getLast4());
                        sessionDetails.setCardExpMonth(paymentMethod.getCard().getExpMonth());
                        sessionDetails.setCardExpYear(paymentMethod.getCard().getExpYear());
                        sessionDetails.setCardholder(paymentMethod.getBillingDetails().getName());
                    }
                }
            }
            // Validar estado de pago y actualizar si es necesario
            if ("paid".equals(session.getPaymentStatus()) && Boolean.TRUE.equals(updateState)) {
                updateStatePagoOrden(idOrden, sessionId, pagoId);
            }

        } catch (StripeException e) {
            log.error("Error al interactuar con la API de Stripe: {}", e.getMessage(), e);
            throw new StripeServiceException("Error al interactuar con la API de Stripe: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            throw new StripeServiceException("Error inesperado: " + e.getMessage(), e);
        }

        return sessionDetails;
    }


    private void updateStatePagoOrden(UUID idOrden, String sesionid, String pagoId) throws DocumentException, IOException, WriterException {
        OrderEntity order = orderRepository.getById(idOrden);
        if (order.getStatepago().getId().equals(Constantes.ESTADO_PAGADO)) return;//valido en caso este entrando demas
        order.setStatepago(new MasterEntity(Constantes.ESTADO_PAGADO));
        order.setState(new MasterEntity(Constantes.ESTADO_ORDEN_PREPARANDO_ENVIO));
        order.setDatepreparation(LocalDateTime.now());
        order.setDatepay(LocalDateTime.now());
        order.setIdsessionstripe(sesionid);
        order.setIdpagostripe(pagoId);
        updateStockProduct(idOrden);//Actualizamos el stock de los productos
        orderRepository.save(order);
        //Obtenemos los archivos a enviar

        sendCorreoOrder(order);//Enviamos el correo
    }

    private void sendCorreoOrder(OrderEntity order) throws IOException, DocumentException, WriterException {
        List<DetailOrderEntity> listdetails = detailOrderRepository.findByOrder(order.getId());
        MultipartFile file = ticketService.createTicketOrder(order, order.getClient(), listdetails);
        List<MultipartFile> lisfiles = new ArrayList<>();
        lisfiles.add(file);
        SunatResponseDTO sunatresponse = billingService.sendSunat(order, listdetails);
        System.out.println("Esta es respuesta de SUNAT " + sunatresponse);
        if (sunatresponse.getCode().equals("0")) {
            MultipartFile fileXml = Util.convertXmlToMultipart(sunatresponse.getXml(), "SunatXML");
            lisfiles.add(fileXml);
        }

        emailService.sendHtmlAndArchivos(order.getClient().getEmail(), "ENVIO DE BOLETA DE VENTA",
                setDataOrdertoDocuement(order),
                lisfiles);
    }

    public String setDataOrdertoDocuement(OrderEntity order) throws IOException {
        Document document = UtilHtml.converHtmltoDocument(htmlorderpay);
        // Modificar el contenido HTML y agregarle valores Dinámicos
        Element nameUserElement = document.selectFirst(".name_user");
        if (Objects.nonNull(nameUserElement)) nameUserElement.text(order.getFirstname());
        Element numberOrderElement = document.selectFirst(".number_order");
        if (Objects.nonNull(numberOrderElement)) numberOrderElement.text(order.getCode());
        Element dateOrdenElement = document.selectFirst(".date_order");
        if (Objects.nonNull(dateOrdenElement))
            dateOrdenElement.text(Util.formatLocalDateTime(order.getDatecreate(), "dd/MM/yyyy"));
        return document.outerHtml();
    }


    //Quitamos el stock
    private void updateStockProduct(UUID idOrden) {
        List<DetailOrderEntity> list = detailOrderRepository.findByOrder(idOrden);
        for (DetailOrderEntity x : list) {
            if (Objects.isNull(x.getProduct())) continue;//En caso sea de envio
            ProductEntity product = productRepository.getById(x.getProduct().getId());
            product.setStock(product.getStock() - x.getQuantity());
            productRepository.save(product);
        }
    }


    public String refundPayment(String chargeId) {
        try {
            Stripe.apiKey = stripeApiKey;

            RefundCreateParams params = RefundCreateParams.builder()
                    .setCharge(chargeId)  // ID del pago que deseas reembolsar
                    .build();
            Refund refund = Refund.create(params);
            return "Reembolso exitoso: " + refund.getId();
        } catch (StripeException e) {
            return "Error al procesar el reembolso: " + e.getMessage();
        }
    }

    // Definimos la excepción personalizada
    public class StripeServiceException extends RuntimeException {
        public StripeServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }


}
