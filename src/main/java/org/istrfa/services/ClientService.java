package org.istrfa.services;

import org.istrfa.dto.*;
import org.istrfa.models.ClientEntity;
import org.istrfa.models.MasterEntity;
import org.istrfa.repositories.ClientRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientService {

    private final ClientRepository repository;

    private final ModelMapper modelMapper;

    @Autowired
    public ClientService(ClientRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }


    @PersistenceContext
    private EntityManager em;
    private CriteriaBuilder cb;
    private CriteriaQuery<ClientEntity> cq;
    private Root<ClientEntity> root;
    private CriteriaQuery<Long> cqCont;
    private Root<ClientEntity> rootCont;


    public void initCb() {
        cb = em.getCriteriaBuilder();
        cq = cb.createQuery(ClientEntity.class);
        root = cq.from(ClientEntity.class);
        cqCont = cb.createQuery(Long.class);
        rootCont = cqCont.from(ClientEntity.class);
    }

    public TypedQuery<ClientEntity> filtrado(ClientFilterDTO filter) {
        initCb();
        cqCont.select(cb.count(rootCont));
        Predicate[] predicatesArray;
        var predicates = new ArrayList<Predicate>();

        PredicateUtil.addLikePredicate(predicates, cb, filter.getFullname(), root.get("fullname"));
        PredicateUtil.addLikePredicate(predicates, cb, filter.getEmail(), root.get("email"));
        PredicateUtil.addEqualUuidPredicate(predicates, cb, filter.getStateId(), root.get("state").get("id"));


        predicatesArray = predicates.toArray(new Predicate[0]);
        cq.where(predicatesArray);
        cqCont.where(predicatesArray);
        cq.select(root).distinct(true);
        cq.orderBy(cb.desc(root.get("datecreate")));
        return em.createQuery(cq);
    }

    public Page<ClientTrayDTO> bandeja(ClientFilterDTO filtro, Integer page, Integer size) {
        TypedQuery<ClientEntity> result = this.filtrado(filtro);
        result = result.setFirstResult(page * size);
        result = result.setMaxResults(size);
        Query resultCont = em.createQuery(cq);
        long all = resultCont.getResultList().size();
        var resultList = result.getResultList();
        List<ClientTrayDTO> response = resultList.stream().map(
                        x -> modelMapper.map(x, ClientTrayDTO.class))
                .collect(Collectors.toList());
        em.close();
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(response, pageable, all);
    }


    public ResponseDTO<ClientDTO> save(ClientPostDTO dto) {
        ClientEntity clientexist;
        if (Objects.isNull(dto.getId())) {
            clientexist = repository.findByEmail(dto.getEmail());
        } else {
            clientexist = repository.findByEmailEdit(dto.getEmail(), dto.getId());
        }
        if (Objects.nonNull(clientexist)){
            ClientDTO dtoexist = modelMapper.map(clientexist, ClientDTO.class);
            return new ResponseDTO<>(0, "El email ingresado ya se encuentra registrado en el sistema.", dtoexist);
        }

        //Contruimos la data del cliente a guardar
        ClientEntity entity = modelMapper.map(dto, ClientEntity.class);
        entity.setActive(1);
        entity.setState(new MasterEntity(Constantes.ESTADO_CLIENTE_ACTIVO));

        //Guardamos en la bd al usuario
        ClientEntity entitySaved = repository.save(entity);
        ClientDTO dtosaved = modelMapper.map(entitySaved, ClientDTO.class);
        return new ResponseDTO<>(1, "Cliente registrado correctamente.", dtosaved);

    }

//    public ClientDTO updateClient(UUID idCLient,ClientPostDTO dto) {
//        ClientEntity entity = repository.getById(idCLient);
//        entity.setName(dto.getName());
//        entity.setLastname(dto.getLastname());
//        entity.setPhone(dto.getPhone());
//        entity.setDistrict(new DistritoEntity(dto.getDistrictId()));
//        entity.setAddress(dto.getAddress());
//        ClientEntity entityupdate = repository.save(entity);
//        return modelMapper.map(entityupdate, ClientDTO.class);
//    }

    public ClientDTO findByid(UUID id) {
        ClientEntity model = repository.getById(id);
        return modelMapper.map(model, ClientDTO.class);
    }

    public ClientDTO findByEmail(String email) {
        ClientEntity model = repository.findByEmail(email);
        return modelMapper.map(model, ClientDTO.class);
    }

}
