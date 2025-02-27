package org.istrfa.services;

import org.istrfa.dto.ApiPeruDni;
import org.istrfa.dto.ApiPeruRuc;
import org.istrfa.dto.PersonDTO;
import org.istrfa.dto.ResponseDTO;
import org.istrfa.models.PersonEntity;
import org.istrfa.repositories.PersonRepository;
import org.istrfa.utils.Constantes;
import org.istrfa.utils.Util;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class PersonService {

    private final PersonRepository repository;

    private final ModelMapper modelMapper;

    private final ApiPeruService apiPeruService;

    @Autowired
    public PersonService(PersonRepository repository, ModelMapper modelMapper, ApiPeruService apiPeruService) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.apiPeruService = apiPeruService;
    }

    public ResponseDTO<PersonDTO> findByNumerodoc(String numdoc, UUID tipyDoc) {
        PersonEntity personbd = repository.findByNumdoc(numdoc);
        if (Objects.nonNull(personbd)) {
            return new ResponseDTO<>(1, "Persona encontrada correctamente en bd", modelMapper.map(personbd, PersonDTO.class));
        }
        if ((Objects.nonNull(tipyDoc) && tipyDoc.equals(Constantes.TIPO_DOCUMENT_DNI)) || (Objects.isNull(tipyDoc) && numdoc.length() == 8)) {
            return getByApiPeruDNI(numdoc);
        }

        if ((Objects.nonNull(tipyDoc) && tipyDoc.equals(Constantes.TIPO_DOCUMENT_RUC)) || (Objects.isNull(tipyDoc) && numdoc.length() == 11)) {
            return getByApiPeruRUC(numdoc);
        }

        return new ResponseDTO<>(0, "No se encontró información.", null);
    }

    public ResponseDTO<PersonDTO> getByApiPeruDNI(String numdoc) {
        ApiPeruDni data = apiPeruService.findByDNI(numdoc);
        if (Objects.isNull(data) || Boolean.FALSE.equals(data.getSuccess()))
            return new ResponseDTO<>(0, "No se encontro información.", null);
        PersonDTO dto = new PersonDTO();
        dto.setNumerodoc(data.getDni());
        dto.setName(data.getNombres());
        dto.setFirstname(data.getApellidoPaterno());
        dto.setLastname(data.getApellidoMaterno());
        dto.setFullname(Util.getFullNamePersonDTO(dto));
        return new ResponseDTO<>(1, "Persona encontrada correctamente en el servicio de apis.", dto);

    }

    public ResponseDTO<PersonDTO> getByApiPeruRUC(String numdoc) {
        ApiPeruRuc data = apiPeruService.findByRUC(numdoc);
        if (Objects.isNull(data) || (Objects.nonNull(data.getSuccess()) && Boolean.FALSE.equals(data.getSuccess())))
            return new ResponseDTO<>(0, "No se encontro información.", null);
        PersonDTO dto = new PersonDTO();
        dto.setNumerodoc(data.getRuc());
        dto.setName(data.getRazonSocial());
        dto.setAddress(data.getDireccion());
        dto.setFullname(data.getRazonSocial());
        return new ResponseDTO<>(1, "Persona encontrada correctamente en el servicio de apis.", dto);

    }


}
