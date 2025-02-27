package org.istrfa.services;

import org.istrfa.dto.*;
import org.istrfa.models.*;
import org.istrfa.repositories.*;
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
public class ProductService {

    private final ProductRepository repository;

    private final ImgnsxProductRepository imgnsxProductRepository;

    private final DescripAditPrdctRepository descripAditPrdctRepository;

    private final DetailOrderRepository detailOrderRepository;

    private final OffersRepository offersRepository;

    private final ModelMapper modelMapper;
    private final TypeMarcaModeloRepository typeMarcaModeloRepository;


    @Autowired
    public ProductService(ProductRepository repository, ImgnsxProductRepository imgnsxProductRepository, DescripAditPrdctRepository descripAditPrdctRepository, DetailOrderRepository detailOrderRepository, OffersRepository offersRepository, ModelMapper modelMapper, TypeMarcaModeloRepository typeMarcaModeloRepository) {
        this.repository = repository;
        this.imgnsxProductRepository = imgnsxProductRepository;
        this.descripAditPrdctRepository = descripAditPrdctRepository;
        this.detailOrderRepository = detailOrderRepository;
        this.offersRepository = offersRepository;
        this.modelMapper = modelMapper;
        this.typeMarcaModeloRepository = typeMarcaModeloRepository;
    }

    @PersistenceContext
    private EntityManager em;
    private CriteriaBuilder cb;
    private CriteriaQuery<ProductEntity> cq;
    private Root<ProductEntity> root;
    private CriteriaQuery<Long> cqCont;
    private Root<ProductEntity> rootCont;


    public void initCb() {
        cb = em.getCriteriaBuilder();
        cq = cb.createQuery(ProductEntity.class);
        root = cq.from(ProductEntity.class);
        cqCont = cb.createQuery(Long.class);
        rootCont = cqCont.from(ProductEntity.class);
    }

    public TypedQuery<ProductEntity> filtrado(ProductFilterDTO filter) {
        initCb();
        cqCont.select(cb.count(rootCont));
        Predicate[] predicatesArray;
        var predicates = new ArrayList<Predicate>();

        PredicateUtil.addLikePredicate(predicates, cb, filter.getName(), root.get("name"));
        PredicateUtil.addLikePredicate(predicates, cb, filter.getType(), root.get("type").get("name"));
        PredicateUtil.addEqualUuidPredicate(predicates, cb, filter.getTypeId(), root.get("type").get("id"));
        PredicateUtil.addLikePredicate(predicates, cb, filter.getMarca(), root.get("marca").get("name"));
        PredicateUtil.addEqualUuidPredicate(predicates, cb, filter.getMarcaId(), root.get("marca").get("id"));

        if(Objects.nonNull(filter.getStatus())){
            if(filter.getStatus().equals("en-stock")){
                predicates.add(cb.greaterThan(root.get("stock"), 0));
            }else{//Vino el texto agotado
                predicates.add(cb.equal(root.get("stock"), 0));
            }
        }

        PredicateUtil.addRangeDoublePredicate(predicates, cb, filter.getPricemin(), filter.getPricemax(), root.get("price"));

        predicates.add(cb.equal(root.get("active"), 1));
        predicatesArray = predicates.toArray(new Predicate[0]);
        cq.where(predicatesArray);
        cqCont.where(predicatesArray);
        cq.select(root).distinct(true);
        cq.orderBy(cb.desc(root.get("datecreate")));
        return em.createQuery(cq);
    }

    public Page<ProductTrayDTO> bandeja(ProductFilterDTO filtro, Integer page, Integer size) {
        TypedQuery<ProductEntity> result = this.filtrado(filtro);
        result = result.setFirstResult(page * size);
        result = result.setMaxResults(size);
        Query resultCont = em.createQuery(cq);
        long all = resultCont.getResultList().size();
        var resultList = result.getResultList();
        List<ProductTrayDTO> response = resultList.stream().map(
                        x -> modelMapper.map(x, ProductTrayDTO.class))
                .collect(Collectors.toList());
        em.close();
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(response, pageable, all);
    }

    public ResponseDTO<ProductDTO> save(ProductPostDTO dto) {
        ProductEntity productExist;
        if (Objects.isNull(dto.getId())) {
            productExist = repository.findByNameAndModelo(dto.getName(), dto.getModeloId());
        } else {
            productExist = repository.findByNameAndModeloEdit(dto.getName(), dto.getModeloId(), dto.getId());
        }
        if (Objects.nonNull(productExist))
            return new ResponseDTO<>(0, "El producto ingresado ya se encuentra registrado.", null);

        //Guardamos el producto
        ProductEntity entity = modelMapper.map(dto, ProductEntity.class);
        entity.setDatecreate(LocalDateTime.now());
        entity.setActive(1);
        setCodeProduct(entity,dto.getTypeId());
        //Obtenemos la data en caso se este actualizando
        if (Objects.nonNull(dto.getId())) {
            ProductEntity dataOld = repository.getById(dto.getId());
            entity.setDatecreate(dataOld.getDatecreate());
            entity.setDatepublication(dataOld.getDatepublication());
            entity.setSku(dataOld.getSku());
            entity.setCode(dataOld.getCode());
        }
        //Seteamos la fecha de publicación
        if (dto.getStatusId().equals(Constantes.ESTADO_PRODUCTO_PUBLICADO)) {
            if (Objects.isNull(entity.getDatepublication()))
                entity.setDatepublication(LocalDateTime.now());//Validamos que sea null, por el tema de actualizacion
        } else {
            entity.setDatepublication(null);//Limpiamos la fecha de publicacion en caso el estado sea borrador
        }
        ProductEntity entitySaved = repository.save(entity);
        saveImagesProduct(dto.getListimages(), entitySaved);
        saveDescriptAditionalsProduct(dto.getListdescripaditionals(), entitySaved);
        return new ResponseDTO<>(1, "Producto registrado correctamente.", modelMapper.map(entitySaved, ProductDTO.class));
    }

    private void saveImagesProduct(List<ImgnsxProductDTO> listimages, ProductEntity entityParent) {
        //Eliminamos los actuales, esto en caso de actualización
        imgnsxProductRepository.deleteByProductId(entityParent.getId());
        List<ImgnsxProductEntity> listsave = listimages.stream().map(
                        x -> {
                            ImgnsxProductEntity entity = modelMapper.map(x, ImgnsxProductEntity.class);
                            entity.setProduct(entityParent);
                            entity.setDatecreate(LocalDateTime.now());
                            entity.setActive(1);
                            return entity;
                        })
                .collect(Collectors.toList());
        imgnsxProductRepository.saveAll(listsave);
    }

    private void saveDescriptAditionalsProduct(List<DescripAditPrdctDTO> listdescripadd, ProductEntity entityParent) {
        //Eliminamos los actuales, esto en caso de actualización
        descripAditPrdctRepository.deleteByProductId(entityParent.getId());
        List<DescripAditPrdctEntity> listsave = listdescripadd.stream().map(
                        x -> {
                            DescripAditPrdctEntity entity = modelMapper.map(x, DescripAditPrdctEntity.class);
                            entity.setProduct(entityParent);
                            entity.setDatecreate(LocalDateTime.now());
                            entity.setActive(1);
                            return entity;
                        })
                .collect(Collectors.toList());
        descripAditPrdctRepository.saveAll(listsave);
    }

    private void setCodeProduct(ProductEntity entity,UUID idType) {
        Integer numCode = repository.getMaxNumSku(idType);
        TypeMarcaModeloEntity type=typeMarcaModeloRepository.getById(idType);
        if (Objects.isNull(numCode)) numCode = 0;
        numCode = numCode + 1;
        String input=type.getName();
        // Tomar los primeros 4 caracteres o la longitud completa si es menor a 4
        String substring = input.length() >= 4 ? input.substring(0, 4) : input;
        //Seteamos el código generado a la orden
        entity.setCode(substring+"-"+numCode);
        entity.setSku(numCode);
    }




    public ProductDTO findByid(UUID id) {
        ProductEntity model = repository.getById(id);
        return modelMapper.map(model, ProductDTO.class);
    }

    public ResponseDTO<Integer> deleteProduct(UUID id){
        ProductEntity model = repository.getById(id);
        model.setActive(0);
        repository.save(model);
        return new ResponseDTO<>(Constantes.HTTP_STATUS_CORRECT,"Producto eliminado correctamente",Constantes.HTTP_STATUS_CORRECT);
    }

    //Lista de productos que más se han vendido
    public List<ProductDTO> getProductTrending() {
        // Obtenemos los resultados como Page<Object[]>
        Page<Object[]> results = detailOrderRepository.findMostRepeatedProducts(PageRequest.of(0, 8));

        // Extraemos los UUIDs del primer elemento de cada fila (columna 0 del Object[])
        List<UUID> productIds = results.stream()
                .map(row -> (UUID) row[0]) // Asumiendo que el ID del producto está en la primera posición
                .collect(Collectors.toList());

        // Obtenemos las entidades ProductEntity por sus IDs
        List<ProductEntity> products = repository.findProductsByIds(productIds);

        // Mapeamos las entidades a DTOs
        return products.stream()
                .map(x -> modelMapper.map(x, ProductDTO.class))
                .collect(Collectors.toList());
    }

    //Obtener la lista de productos en oferta
    public List<ProductDTO> getProductOffers() {
        List<OffersEntity> listoffers=offersRepository.findOffersByDate(LocalDateTime.now(),Constantes.ESTADO_OFERTA_ACTIVO);
        return listoffers.stream()
                .map(x -> {
                    ProductDTO dto=modelMapper.map(x.getProduct(), ProductDTO.class);
                    dto.setDiscount(x.getDiscount());
                    dto.setDateofferend(x.getDateend());
                    return dto;
                })
                .collect(Collectors.toList());
    }



    }
