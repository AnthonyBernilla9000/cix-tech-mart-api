package org.istrfa.services;

import org.istrfa.dto.*;
import org.istrfa.models.MasterEntity;
import org.istrfa.models.PersonEntity;
import org.istrfa.models.WorkerEntity;
import org.istrfa.repositories.PersonRepository;
import org.istrfa.repositories.UserRepository;
import org.istrfa.utils.Constantes;
import org.istrfa.utils.PredicateUtil;
import org.istrfa.utils.Util;
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
public class WorkerService {

    private final UserRepository repository;

    private final PersonRepository personRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public WorkerService(UserRepository repository, ModelMapper modelMapper, PersonRepository personRepository) {
        this.repository = repository;
        this.personRepository = personRepository;
        this.modelMapper = modelMapper;
    }

    @PersistenceContext
    private EntityManager em;
    private CriteriaBuilder cb;
    private CriteriaQuery<WorkerEntity> cq;
    private Root<WorkerEntity> root;
    private CriteriaQuery<Long> cqCont;
    private Root<WorkerEntity> rootCont;


    public void initCb() {
        cb = em.getCriteriaBuilder();
        cq = cb.createQuery(WorkerEntity.class);
        root = cq.from(WorkerEntity.class);
        cqCont = cb.createQuery(Long.class);
        rootCont = cqCont.from(WorkerEntity.class);
    }

    public TypedQuery<WorkerEntity> filtrado(WorkerFilterDTO filter) {
        initCb();
        cqCont.select(cb.count(rootCont));
        Predicate[] predicatesArray;
        var predicates = new ArrayList<Predicate>();

        PredicateUtil.concatPerson(predicates, "person", cb, root, filter.getName());
        PredicateUtil.addLikePredicate(predicates, cb, filter.getEmail(), root.get("email"));
        PredicateUtil.addEqualUuidPredicate(predicates, cb, filter.getStateId(), root.get("state").get("id"));


        predicatesArray = predicates.toArray(new Predicate[0]);
        cq.where(predicatesArray);
        cqCont.where(predicatesArray);
        cq.select(root).distinct(true);
        cq.orderBy(cb.desc(root.get("datecreate")));
        return em.createQuery(cq);
    }

    public Page<WorkerTrayDTO> bandeja(WorkerFilterDTO filtro, Integer page, Integer size) {
        TypedQuery<WorkerEntity> result = this.filtrado(filtro);
        result = result.setFirstResult(page * size);
        result = result.setMaxResults(size);
        Query resultCont = em.createQuery(cq);
        long all = resultCont.getResultList().size();
        var resultList = result.getResultList();
        List<WorkerTrayDTO> response = resultList.stream().map(
                x -> {
                    WorkerTrayDTO dto = modelMapper.map(x, WorkerTrayDTO.class);
                    dto.setFullname(Util.getFullNamePersonEntity(x.getPerson()));
                    //Datos propios de la tabla usuarios
                    dto.setId(x.getId());
                    dto.setNumerodoc(x.getPerson().getNumerodoc());
                    dto.setPhoto(x.getPhoto());
                    dto.setStateId(x.getState().getId());
                    dto.setStateName(x.getState().getName());
                    dto.setTypeId(x.getType().getId());
                    dto.setTypeName(x.getType().getName());
                    dto.setEmail(x.getEmail());
                    dto.setDatecreate(x.getDatecreate());
                    return dto;
                }
        ).collect(Collectors.toList());
        em.close();
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(response, pageable, all);
    }

    private WorkerDTO buildDataUser(WorkerEntity x) {
        WorkerDTO dto = modelMapper.map(x.getPerson(), WorkerDTO.class);
        dto.setFullname(Util.getFullNamePersonEntity(x.getPerson()));
        //Datos propios de la tabla trabajadores
        dto.setId(x.getId());
        dto.setPhoto(x.getPhoto());
        dto.setStateId(x.getState().getId());
        dto.setStateName(x.getState().getName());
        dto.setTypeId(x.getType().getId());
        dto.setTypeName(x.getType().getName());
        dto.setEmail(x.getEmail());
        dto.setDatecreate(x.getDatecreate());
        dto.setAuthid(x.getAuthid());
        return dto;
    }

    public ResponseDTO<WorkerDTO> save(WorkerPostDTO dto) {
        ResponseDTO<WorkerDTO> valid = validWorkExist(dto);
        if (valid.getCode() == 0) return valid;
        //Contruimos la data del usuario a guardar
        WorkerEntity entity = new WorkerEntity();
        entity.setId(dto.getId());
        entity.setPerson(savePerson(dto));//Guardamos o actualizamos la persona, aqui limpiamos el id del dto para evitar conflictos
        //Guardamos la data del usuario
        entity.setPhoto(dto.getPhoto());
        entity.setState(new MasterEntity(Constantes.ESTADO_TRABAJADOR_ACTIVO));
        entity.setType(new MasterEntity(dto.getTypeId()));
        entity.setAuthid(dto.getAuthid());
        entity.setEmail(dto.getEmail());
        entity.setDatecreate(LocalDateTime.now());
        if (Objects.nonNull(dto.getId())) {
            WorkerEntity dataOld = repository.getById(dto.getId());
            entity.setDatecreate(dataOld.getDatecreate());
            entity.setState(dataOld.getState());
        }
        //Guardamos en la bd al usuario
        WorkerEntity entitySaved = repository.save(entity);
        WorkerDTO dtosave = buildDataUser(entitySaved);
        return new ResponseDTO<>(1, "Usuario registrado correctamente.", dtosave);

    }

    private ResponseDTO<WorkerDTO> validWorkExist(WorkerPostDTO dto) {
        WorkerEntity userexist;
        WorkerEntity userexistEmail;
        if (Objects.isNull(dto.getId())) {
            userexist = repository.findByNumdoc(dto.getNumerodoc());
            userexistEmail = repository.findByEmail(dto.getEmail());
        } else {
            userexist = repository.findByNumdocEdit(dto.getNumerodoc(), dto.getId());
            userexistEmail = repository.findByEmailEdit(dto.getEmail(), dto.getId());
        }
        if (Objects.nonNull(userexist))
            return new ResponseDTO<>(0, "El usuario con el documento " + dto.getNumerodoc() + " ya se encuentra registrado.", null);
        if (Objects.nonNull(userexistEmail))
            return new ResponseDTO<>(0, "El usuario con el correo " + dto.getEmail() + " ya se encuentra registrado.", null);
        return new ResponseDTO<>(1, "Conforme", null);
    }

    private PersonEntity savePerson(WorkerPostDTO userdto) {
        userdto.setId(null);//Limpiamos el id, ya que es del usuario y no de la persona
        PersonEntity persona = modelMapper.map(userdto, PersonEntity.class);
        PersonEntity personexist = personRepository.findByNumdoc(userdto.getNumerodoc());
        if (Objects.nonNull(personexist)) {
            persona.setId(personexist.getId());
            persona.setDatecreate(personexist.getDatecreate());
        }
        return personRepository.save(persona);
    }

    public ResponseDTO<Integer> updateWorker(UUID idWorker, WorkerPostDTO dto) {
        WorkerEntity entity = repository.getById(idWorker);
        entity.setType(new MasterEntity(dto.getTypeId()));
        repository.save(entity);
        PersonEntity person = personRepository.getById(entity.getPerson().getId());
        person.setPhone(dto.getPhone());
        person.setAddress(dto.getAddress());
        personRepository.save(person);
        return new ResponseDTO<>(Constantes.HTTP_STATUS_CORRECT, "Trabajador actualizado correctamente", Constantes.HTTP_STATUS_CORRECT);
    }

    public WorkerDTO findByid(UUID id) {
        WorkerEntity model = repository.getById(id);
        return buildDataUser(model);
    }

    public WorkerDTO findByEmail(String email) {
        WorkerEntity model = repository.findByEmail(email);
        System.out.println("Este es el authid " + model.getAuthid());
        return buildDataUser(model);
    }


}
