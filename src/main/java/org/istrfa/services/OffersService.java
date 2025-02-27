package org.istrfa.services;

import org.istrfa.dto.*;
import org.istrfa.models.MasterEntity;
import org.istrfa.models.OffersEntity;
import org.istrfa.repositories.OffersRepository;
import org.istrfa.utils.Constantes;
import org.istrfa.utils.PredicateUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OffersService {

    private final OffersRepository repository;
    private final ModelMapper modelMapper;

    @Autowired
    public OffersService(OffersRepository offersRepository, ModelMapper modelMapper) {
        this.repository = offersRepository;
        this.modelMapper = modelMapper;
    }


    @PersistenceContext
    private EntityManager em;
    private CriteriaBuilder cb;
    private CriteriaQuery<OffersEntity> cq;
    private Root<OffersEntity> root;
    private CriteriaQuery<Long> cqCont;
    private Root<OffersEntity> rootCont;


    public void initCb() {
        cb = em.getCriteriaBuilder();
        cq = cb.createQuery(OffersEntity.class);
        root = cq.from(OffersEntity.class);
        cqCont = cb.createQuery(Long.class);
        rootCont = cqCont.from(OffersEntity.class);
    }

    public TypedQuery<OffersEntity> filtrado(OffersFilterDTO filter) {
        initCb();
        cqCont.select(cb.count(rootCont));
        Predicate[] predicatesArray;
        var predicates = new ArrayList<Predicate>();

        PredicateUtil.addLikePredicate(predicates, cb, filter.getProductName(), root.get("product").get("name"));
        PredicateUtil.addBetweenPredicate(predicates, cb, filter.getDatestart(), filter.getDateend(), root.get("datecreate"));
        PredicateUtil.addEqualUuidPredicate(predicates, cb, filter.getStatusId(), root.get("status").get("id"));
        predicates.add(cb.equal(root.get("active"), 1));

        predicatesArray = predicates.toArray(new Predicate[0]);
        cq.where(predicatesArray);
        cqCont.where(predicatesArray);
        cq.select(root).distinct(true);
        cq.orderBy(cb.desc(root.get("datecreate")));
        return em.createQuery(cq);
    }

    public Page<OffersTrayDTO> bandeja(OffersFilterDTO filtro, Integer page, Integer size) {
        TypedQuery<OffersEntity> result = this.filtrado(filtro);
        result = result.setFirstResult(page * size);
        result = result.setMaxResults(size);
        Query resultCont = em.createQuery(cq);
        long all = resultCont.getResultList().size();
        var resultList = result.getResultList();
        List<OffersTrayDTO> response = resultList.stream().map(
                        x -> modelMapper.map(x, OffersTrayDTO.class))
                .collect(Collectors.toList());
        em.close();
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(response, pageable, all);
    }

    public ResponseDTO<OffersDTO> save(OffersPostDTO dto) {

        OffersEntity offerExist;
        if (Objects.isNull(dto.getId())) {
            offerExist = repository.findOffersExist(dto.getDatestart(),dto.getDateend(),dto.getProductId());
        } else {
            offerExist = repository.findOffersExistEdit(dto.getDatestart(),dto.getDateend(),dto.getProductId(), dto.getId());
        }
        if (Objects.nonNull(offerExist))
            return new ResponseDTO<>(Constantes.HTTP_STATUS_VALIDATE, "Ya existe una oferta registrada que contiene parte del rango de fechas ingresado.", null);

        //Guardamos la oferta
        OffersEntity entity = modelMapper.map(dto, OffersEntity.class);
        entity.setDatecreate(LocalDateTime.now());
        entity.setActive(1);
        entity.setStatus(new MasterEntity(Constantes.ESTADO_OFERTA_ACTIVO));
        if (Objects.nonNull(dto.getId())) {
            OffersEntity dataOld = repository.getById(dto.getId());
            entity.setDatecreate(dataOld.getDatecreate());
            entity.setStatus(dataOld.getStatus());
        }
        OffersEntity entitySaved=repository.save(entity);
        return new ResponseDTO<>(Constantes.HTTP_STATUS_CORRECT, "Oferta registrada correctamente.", modelMapper.map(entitySaved, OffersDTO.class));
    }

    public ResponseDTO<Integer> updateState(UUID idOffer,UUID state){
        OffersEntity entity = repository.getById(idOffer);
        entity.setStatus(new MasterEntity(state));
        repository.save(entity);
        return new ResponseDTO<>(Constantes.HTTP_STATUS_CORRECT, "Estado actualizado correctamente.", Constantes.HTTP_STATUS_CORRECT);
    }


}
