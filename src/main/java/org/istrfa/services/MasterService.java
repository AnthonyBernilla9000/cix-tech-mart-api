package org.istrfa.services;

import org.istrfa.dto.MasterDTO;
import org.istrfa.repositories.MasterRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MasterService {

    private final MasterRepository repository;

    private final ModelMapper modelMapper;

    @Autowired
    public MasterService(MasterRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }



    public List<MasterDTO> findByPrefix(Integer prefix) {
        return repository.findByPrefix(prefix).stream().map(x ->
                modelMapper.map(x, MasterDTO.class)).collect(Collectors.toList());
    }

    public List<MasterDTO> findByPrefixAndCorrelatives(Integer prefix, Integer[] correlatives) {
        return repository.findByPrefixAndCorrelatives(prefix, correlatives).stream().map(x ->
                modelMapper.map(x, MasterDTO.class)
        ).collect(Collectors.toList());
    }

}
