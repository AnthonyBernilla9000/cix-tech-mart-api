package org.istrfa.services;

import org.istrfa.dto.TypeMarcaModeloDTO;
import org.istrfa.models.TypeMarcaModeloEntity;
import org.istrfa.repositories.TypeMarcaModeloRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class TypeMarcaModeloService {

    private final TypeMarcaModeloRepository repository;

    private final ModelMapper modelMapper;

    @Autowired
    public TypeMarcaModeloService(TypeMarcaModeloRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    public List<TypeMarcaModeloDTO> getAllTypes() {
        List<TypeMarcaModeloEntity> list = repository.getAllTypes();
        return list.stream().map(
                        x -> modelMapper.map(x, TypeMarcaModeloDTO.class))
                .collect(Collectors.toList());
    }

    public List<TypeMarcaModeloDTO> getMarcasByType(UUID idType) {
        List<TypeMarcaModeloEntity> list = repository.getMarcaByType(idType);
        return list.stream().map(
                        x -> modelMapper.map(x, TypeMarcaModeloDTO.class))
                .collect(Collectors.toList());
    }

    public List<TypeMarcaModeloDTO> getModelosByMarca(UUID idMarca) {
        List<TypeMarcaModeloEntity> list = repository.getModeloByMarca(idMarca);
        return list.stream().map(
                        x -> modelMapper.map(x, TypeMarcaModeloDTO.class))
                .collect(Collectors.toList());
    }


}
