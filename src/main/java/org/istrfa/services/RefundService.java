package org.istrfa.services;

import org.istrfa.dto.*;
import org.istrfa.models.*;
import org.istrfa.repositories.DetailOrderRepository;
import org.istrfa.repositories.OrderRepository;
import org.istrfa.repositories.ProductRepository;
import org.istrfa.repositories.RefundRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class RefundService {

    private final RefundRepository repository;

    private final ModelMapper modelMapper;
    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final StripeService stripeService;
    private final DetailOrderRepository detailOrderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public RefundService(RefundRepository repository, ModelMapper modelMapper, OrderRepository orderRepository, EmailService emailService, StripeService stripeService, DetailOrderRepository detailOrderRepository, ProductRepository productRepository) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.orderRepository = orderRepository;
        this.emailService = emailService;
        this.stripeService = stripeService;
        this.detailOrderRepository = detailOrderRepository;
        this.productRepository = productRepository;
    }

    @Value("${templates.solit-refund}")
    private String htmlsolitRefund;

    @Value("${templates.refund-aproveed}")
    private String htmlRefundAproveed;


    @PersistenceContext
    private EntityManager em;
    private CriteriaBuilder cb;
    private CriteriaQuery<RefundEntity> cq;
    private Root<RefundEntity> root;
    private CriteriaQuery<Long> cqCont;
    private Root<RefundEntity> rootCont;


    public void initCb() {
        cb = em.getCriteriaBuilder();
        cq = cb.createQuery(RefundEntity.class);
        root = cq.from(RefundEntity.class);
        cqCont = cb.createQuery(Long.class);
        rootCont = cqCont.from(RefundEntity.class);
    }

    public TypedQuery<RefundEntity> filtrado(RefundFilterDTO filter) {
        initCb();
        cqCont.select(cb.count(rootCont));
        Predicate[] predicatesArray;
        var predicates = new ArrayList<Predicate>();

        PredicateUtil.addLikePredicate(predicates, cb, filter.getOrderCode(), root.get("order").get("code"));
        PredicateUtil.addEqualUuidPredicate(predicates, cb, filter.getStateId(), root.get("state").get("id"));
        PredicateUtil.addEqualUuidPredicate(predicates, cb, filter.getResultId(), root.get("result").get("id"));
        PredicateUtil.addBetweenPredicate(predicates, cb, filter.getDatestart(), filter.getDateend(), root.get("datecreate"));

        predicates.add(cb.equal(root.get("active"), 1));
        predicatesArray = predicates.toArray(new Predicate[0]);
        cq.where(predicatesArray);
        cqCont.where(predicatesArray);
        cq.select(root).distinct(true);
        cq.orderBy(cb.desc(root.get("datecreate")));
        return em.createQuery(cq);
    }

    public Page<RefundTrayDTO> bandeja(RefundFilterDTO filtro, Integer page, Integer size) {
        TypedQuery<RefundEntity> result = this.filtrado(filtro);
        result = result.setFirstResult(page * size);
        result = result.setMaxResults(size);
        Query resultCont = em.createQuery(cq);
        long all = resultCont.getResultList().size();
        var resultList = result.getResultList();
        List<RefundTrayDTO> response = resultList.stream().map(
                        x -> modelMapper.map(x, RefundTrayDTO.class))
                .collect(Collectors.toList());
        em.close();
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(response, pageable, all);
    }


    public ResponseDTO<Integer> generateSolRefund(RefundPostDTO dto) throws IOException {

        RefundEntity refundExist = repository.findByOrderState(dto.getOrderId(), Constantes.ESTADO_REEMBOLSO_ENVIADO);
        if (Objects.nonNull(refundExist))
            return new ResponseDTO<>(Constantes.HTTP_STATUS_VALIDATE, "Ya ha realizado una solicitud.", Constantes.HTTP_STATUS_VALIDATE);
        RefundEntity entity = modelMapper.map(dto, RefundEntity.class);
        entity.setDatecreate(LocalDateTime.now());
        entity.setActive(1);
        entity.setState(new MasterEntity(Constantes.ESTADO_REEMBOLSO_ENVIADO));
        RefundEntity entitySaved = repository.save(entity);
        sendCorreoRefund(entitySaved.getId());
        return new ResponseDTO<>(Constantes.HTTP_STATUS_CORRECT, "Solicitud de reembolso generada correctamente", Constantes.HTTP_STATUS_CORRECT);
    }


    private void sendCorreoRefund(UUID idRefund) throws IOException {
        RefundEntity refund = repository.getById(idRefund);
        OrderEntity order = orderRepository.getById(refund.getOrder().getId());
        emailService.sendHtmlAndArchivos(order.getClient().getEmail(), "Solicitud de reembolso",
                setDataRefundtoDocuement(refund, order),
                null);
    }

    public String setDataRefundtoDocuement(RefundEntity refund, OrderEntity order) throws IOException {
        Document document = UtilHtml.converHtmltoDocument(htmlsolitRefund);
        //        // Modificar el contenido HTML y agregarle valores Dinámicos
        Element nameUserElement = document.selectFirst(".name_user");
        if (Objects.nonNull(nameUserElement)) nameUserElement.text(order.getClient().getFullname());
        Element numberOrdenElement = document.selectFirst(".number_order");
        if (Objects.nonNull(numberOrdenElement)) numberOrdenElement.text(order.getCode());
        Element cantArtcElement = document.selectFirst(".date_solit");
        if (Objects.nonNull(cantArtcElement))
            cantArtcElement.text(Util.formatLocalDateTime(refund.getDatecreate(), "dd/MM/yyyy"));
        return document.outerHtml();
    }

    public ResponseDTO<Integer> evaluateSolRefund(RefundEvaluateDTO dto) throws IOException {
        RefundEntity entity = repository.getById(dto.getRefundId());
        entity.setResult(new MasterEntity(dto.getResult()));
        entity.setReply(dto.getReply());
        entity.setDatereview(LocalDateTime.now());
        entity.setState(new MasterEntity(Constantes.ESTADO_REEMBOLSO_EVALUADO));
        entity.setWorker(new WorkerEntity(dto.getWorkerId()));
        repository.save(entity);
        if (dto.getResult().equals(Constantes.RESULTADO_REEMBOLSO_APROBADO)) {
            OrderEntity order = orderRepository.getById(entity.getOrder().getId());
            order.setState(new MasterEntity(Constantes.ESTADO_ORDEN_DEVUELTO));
            updateStockProduct(entity.getOrder().getId());//Regresamos el stock a los productos
            stripeService.refundPayment(order.getIdpagostripe());//Realizar reembolso en stripe
            sendCorreoRefundAproveed(order, "Reembolso denegado",
                    "ACEPTADA", "Se te realizará el reembolso a tu tarjeta en un máximo de 48 horas."); //Enviamos el correo confirmando el reembolso
        } else {
            OrderEntity order = orderRepository.getById(entity.getOrder().getId());
            sendCorreoRefundAproveed(order, "Reembolso aceptado",
                    "DENEGADA", "Lo sentimos, pero no es posible realizar el reembolso."); //Enviamos el correo confirmando el reembolso
        }

        return new ResponseDTO<>(Constantes.HTTP_STATUS_CORRECT, "Solicitud de reembolso evaluada correctamente", Constantes.HTTP_STATUS_CORRECT);
    }

    //Retornamos el stock cuando se apruebe el reembolso
    private void updateStockProduct(UUID idOrden) {
        List<DetailOrderEntity> list = detailOrderRepository.findByOrder(idOrden);
        for (DetailOrderEntity x : list) {
            ProductEntity product = productRepository.getById(x.getProduct().getId());
            product.setStock(product.getStock() + x.getQuantity());
            productRepository.save(product);
        }
    }


    private void sendCorreoRefundAproveed(OrderEntity order, String asunto, String result, String comentario) throws IOException {
        emailService.sendHtmlAndArchivos(order.getClient().getEmail(), asunto,
                setDataRefunAproveedDocuement(order, result, comentario),
                null);
    }

    public String setDataRefunAproveedDocuement(OrderEntity order, String result, String comentario) throws IOException {
        Document document = UtilHtml.converHtmltoDocument(htmlRefundAproveed);
        // Modificar el contenido HTML y agregarle valores Dinámicos
        Element resultElement = document.selectFirst(".result_text");
        if (Objects.nonNull(resultElement)) resultElement.text(result);
        Element commentElement = document.selectFirst(".coment_text");
        if (Objects.nonNull(commentElement)) commentElement.text(comentario);

        Element nameUserElement = document.selectFirst(".code_order");
        if (Objects.nonNull(nameUserElement)) nameUserElement.text(order.getCode());
        Element numberOrdenElement = document.selectFirst(".total_refund");
        if (Objects.nonNull(numberOrdenElement)) numberOrdenElement.text(order.getTotal().toString());
        return document.outerHtml();
    }


}
