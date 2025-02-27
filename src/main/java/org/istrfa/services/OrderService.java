package org.istrfa.services;

import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;
import com.stripe.exception.StripeException;
import org.istrfa.dto.*;
import org.istrfa.models.*;
import org.istrfa.repositories.ClientRepository;
import org.istrfa.repositories.DetailOrderRepository;
import org.istrfa.repositories.OrderRepository;
import org.istrfa.utils.Constantes;
import org.istrfa.utils.PredicateUtil;
import org.istrfa.utils.Util;
import org.istrfa.utils.UtilHtml;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository repository;

    private final ModelMapper modelMapper;
    private final DetailOrderRepository detailOrderRepository;
    private final EmailService emailService;
    private final ClientRepository clientRepository;
    private final StripeService stripeService;


    @Value("${templates.order-succes}")
    private String htmlorder;

    @Value("${templates.order-send}")
    private String htmlordersend;

    @Autowired
    public OrderService(OrderRepository repository, ModelMapper modelMapper, DetailOrderRepository detailOrderRepository, EmailService emailService, ClientRepository clientRepository, StripeService stripeService) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.detailOrderRepository = detailOrderRepository;
        this.emailService = emailService;
        this.clientRepository = clientRepository;
        this.stripeService = stripeService;
    }

    @PersistenceContext
    private EntityManager em;
    private CriteriaBuilder cb;
    private CriteriaQuery<OrderEntity> cq;
    private Root<OrderEntity> root;
    private CriteriaQuery<Long> cqCont;
    private Root<OrderEntity> rootCont;


    public void initCb() {
        cb = em.getCriteriaBuilder();
        cq = cb.createQuery(OrderEntity.class);
        root = cq.from(OrderEntity.class);
        cqCont = cb.createQuery(Long.class);
        rootCont = cqCont.from(OrderEntity.class);
    }

    public TypedQuery<OrderEntity> filtrado(OrderFilterDTO filter) {
        initCb();
        cqCont.select(cb.count(rootCont));
        Predicate[] predicatesArray;
        var predicates = new ArrayList<Predicate>();

        PredicateUtil.addLikePredicate(predicates, cb, filter.getCode(), root.get("code"));
        PredicateUtil.concatNamePersonOrden(predicates, cb, root, filter.getPersonfullname());
        PredicateUtil.addEqualUuidPredicate(predicates, cb, filter.getStateId(), root.get("state").get("id"));
        PredicateUtil.addEqualUuidPredicate(predicates, cb, filter.getStatepagoId(), root.get("statepago").get("id"));
        PredicateUtil.addBetweenPredicate(predicates, cb, filter.getDatestart(), filter.getDateend(), root.get("datecreate"));

        predicates.add(cb.equal(root.get("active"), 1));
        predicatesArray = predicates.toArray(new Predicate[0]);
        cq.where(predicatesArray);
        cqCont.where(predicatesArray);
        cq.select(root).distinct(true);
        cq.orderBy(cb.desc(root.get("datecreate")));
        return em.createQuery(cq);
    }

    public Page<OrderTrayDTO> bandeja(OrderFilterDTO filtro, Integer page, Integer size) {
        TypedQuery<OrderEntity> result = this.filtrado(filtro);
        result = result.setFirstResult(page * size);
        result = result.setMaxResults(size);
        Query resultCont = em.createQuery(cq);
        long all = resultCont.getResultList().size();
        var resultList = result.getResultList();
        List<OrderTrayDTO> response = resultList.stream().map(
                        x -> {
                            OrderTrayDTO dto = modelMapper.map(x, OrderTrayDTO.class);
                            dto.setPersonfullname(x.getFirstname() + " " + x.getLastname());
                            return dto;
                        })
                .collect(Collectors.toList());
        em.close();
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(response, pageable, all);
    }

    public ResponseDTO<OrderDTO> saveOrder(OrderPostDTO dto) throws DocumentException, IOException, WriterException {
        OrderEntity entity = modelMapper.map(dto, OrderEntity.class);
        entity.setActive(1);
        entity.setDatecreate(LocalDateTime.now());
        setCodeOrder(entity);
        entity.setState(new MasterEntity(Constantes.ESTADO_ORDEN_GENERADO));
        entity.setStatepago(new MasterEntity(Constantes.ESTADO_POR_PAGAR));
        if (Objects.nonNull(dto.getId())) {
            OrderEntity dataOld = repository.getById(dto.getId());
            entity.setDatecreate(dataOld.getDatecreate());
            entity.setCode(dataOld.getCode());
            entity.setNumcode(dataOld.getNumcode());
            entity.setState(dataOld.getState());
        }
        OrderEntity entitySaved = repository.save(entity);
        saveDetailsOrder(dto.getListdetails(), entitySaved);
        sendCorreoOrderSucces(entitySaved.getId());//Enviamos el correo
        updateClient(dto);//Actualizamos la información del cliente con los datos colocados en la orden
        return new ResponseDTO<>(1, "Orden registrada correctamente.", modelMapper.map(entitySaved, OrderDTO.class));
    }

    private void saveDetailsOrder(List<DetailOrderPostDTO> listdetails, OrderEntity order) {
        //Costruimos la lista de subdetalles y guardamos
        List<DetailOrderEntity> listsave = listdetails.stream().map(
                        x -> {
                            DetailOrderEntity entity = modelMapper.map(x, DetailOrderEntity.class);
                            entity.setTotal(x.getUnitprice() * x.getQuantity());
                            entity.setOrder(order);
                            entity.setDatecreate(LocalDateTime.now());
                            entity.setActive(1);
                            return entity;
                        })
                .collect(Collectors.toList());
        detailOrderRepository.saveAll(listsave);

        //Actualizamos el subtotal, igv y total de la orden
        //Obtenemos el subTotal de la orden
        double totalOrder = listdetails.stream()
                .mapToDouble(x -> x.getQuantity() * x.getUnitprice())
                .sum();
        double subtotal = totalOrder / (1 + 0.18);//Obtenemos el sub total(base imponible)
        double igv = subtotal * 0.18;
        order.setSubtotal(subtotal);
        order.setIgv(igv);
        order.setTotal(totalOrder);
        repository.save(order);
    }

    private void sendCorreoOrderSucces(UUID idOrder) throws IOException {
        OrderEntity order = repository.getById(idOrder);
        Integer listdetails = detailOrderRepository.getTotalItemsfindByOrder(idOrder);
        ClientEntity client = clientRepository.getById(order.getClient().getId());//Lo obtengo de esta manera, debido a que directamente la orden no me da el resto de datos
        emailService.sendHtmlAndArchivos(client.getEmail(), "PEDIDO REALIZADO",
                setDataOrdertoDocuementSucces(order, listdetails),
                null);
    }

    public String setDataOrdertoDocuementSucces(OrderEntity order, Integer cantArtic) throws IOException {
        Document document = UtilHtml.converHtmltoDocument(htmlorder);
        //        // Modificar el contenido HTML y agregarle valores Dinámicos
        Element nameUserElement = document.selectFirst(".name_user");
        if (Objects.nonNull(nameUserElement)) nameUserElement.text(order.getFirstname());
        Element numberOrdenElement = document.selectFirst(".number_orden");
        if (Objects.nonNull(numberOrdenElement)) numberOrdenElement.text(order.getCode());
        Element cantArtcElement = document.selectFirst(".cant_art");
        if (Objects.nonNull(cantArtcElement)) cantArtcElement.text(cantArtic.toString());
        return document.outerHtml();
    }


    public void updateClient(OrderPostDTO orderdto) {
        ClientEntity entity = clientRepository.getById(orderdto.getClientId());
        entity.setName(orderdto.getFirstname());
        entity.setLastname(orderdto.getLastname());
        entity.setPhone(orderdto.getPhone());
        entity.setDistrict(new DistritoEntity(orderdto.getDistritoId()));
        entity.setAddress(orderdto.getAddress());
        clientRepository.save(entity);
    }


    public OrderDTO findByid(UUID id) throws StripeException {
        OrderEntity model = repository.getById(id);
        List<DetailOrderEntity> listdetailt = detailOrderRepository.findByOrder(id);
        List<DetailOrderDTO> listdto = listdetailt.stream().map(
                        x -> modelMapper.map(x, DetailOrderDTO.class))
                .collect(Collectors.toList());
        OrderDTO dto = modelMapper.map(model, OrderDTO.class);
        dto.setListdetails(listdto);
        dto.setPersonfullname(model.getFirstname() + " " + model.getLastname());
        if (Objects.nonNull(model.getIdsessionstripe())) setDataSession(dto, model.getIdsessionstripe());
        return dto;
    }

    private void setDataSession(OrderDTO order, String idSesion) throws StripeException {
        SessionStripeDTO session = stripeService.getSessionDetails(idSesion, null, false);
        if (Objects.isNull(session)) return;
        order.setCodetransaction("#" + idSesion.substring(idSesion.length() - 10));
        order.setPaymentmethod(session.getPaymentMethodType());
        order.setCardholder(session.getCardholder());
        order.setNumbercard("XXXX XXXX XXXX " + session.getCardLast4());
        order.setTotalmount(Double.valueOf(session.getAmountTotal()) / 100.0); //Lo divido entre 100, ya que stripe me lo devuelve en centavos, ejemp 152.10 lo trae como 15210
    }

    private void setCodeOrder(OrderEntity entity) {
        Integer numCode = repository.getMaxNumCode();
        if (Objects.isNull(numCode)) numCode = 0;
        numCode = numCode + 1;
        // Obtener el año actual
        String currentYear = Year.now().toString();
        String code = "ORD-" + currentYear + "-" + numCode;
        //Seteamos el codigo generado a la orden
        entity.setCode(code);
        entity.setNumcode(numCode);
    }

    public List<OrderTrayDTO> getOrdenesByClient(UUID clientId) {
        List<OrderEntity> listorders = repository.getByClientId(clientId);
        return listorders.stream().map(
                        x -> {
                            OrderTrayDTO dto = modelMapper.map(x, OrderTrayDTO.class);
                            dto.setPersonfullname(x.getFirstname() + " " + x.getLastname());
                            return dto;
                        })
                .collect(Collectors.toList());
    }

    public ResponseDTO<Integer> cancelOrder(UUID idOrder) {
        OrderEntity entity = repository.getById(idOrder);
        entity.setState(new MasterEntity(Constantes.ESTADO_ORDEN_CANCELADO));
        repository.save(entity);
        return new ResponseDTO<>(Constantes.HTTP_STATUS_CORRECT, "Orden cancelada correctamente.", Constantes.HTTP_STATUS_CORRECT);
    }

    public ResponseDTO<Integer> sendOrder(UUID idOrder) throws IOException {
        OrderEntity entity = repository.getById(idOrder);
        entity.setState(new MasterEntity(Constantes.ESTADO_ORDEN_EN_TRANSITO));
        entity.setDateenvio(LocalDateTime.now());
        repository.save(entity);
        sendCorreoOrder(entity);
        return new ResponseDTO<>(Constantes.HTTP_STATUS_CORRECT, "Pedido enviado correctamente.", Constantes.HTTP_STATUS_CORRECT);
    }

    private void sendCorreoOrder(OrderEntity order) throws IOException {
        emailService.sendHtmlAndArchivos(order.getClient().getEmail(), "PEDIDO REALIZADO",
                setDataOrdertoDocuement(order),
                null);
    }

    public String setDataOrdertoDocuement(OrderEntity order) throws IOException {
        Document document = UtilHtml.converHtmltoDocument(htmlordersend);
        //        // Modificar el contenido HTML y agregarle valores Dinámicos
        Element nameUserElement = document.selectFirst(".name_user");
        if (Objects.nonNull(nameUserElement)) nameUserElement.text(order.getFirstname());
        Element numberOrdenElement = document.selectFirst(".number_order");
        if (Objects.nonNull(numberOrdenElement)) numberOrdenElement.text(order.getCode());
        Element dateSendElement = document.selectFirst(".send_date");
        if (Objects.nonNull(dateSendElement))
            dateSendElement.text(Util.formatLocalDateTime(order.getDateenvio(), "dd/MM/yyyy"));
        Element cantArtcElement = document.selectFirst(".delivery_date");
        if (Objects.nonNull(cantArtcElement))
            cantArtcElement.text(Util.formatLocalDateTime(order.getDateenvio().plusDays(2), "dd/MM/yyyy"));
        return document.outerHtml();
    }

}
