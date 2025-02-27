package org.istrfa.services;


import org.istrfa.dto.DepartamentoDTO;
import org.istrfa.dto.DistritoDTO;
import org.istrfa.dto.ProvinciaDTO;
import org.istrfa.models.DepartamentoEntity;
import org.istrfa.models.DistritoEntity;
import org.istrfa.models.ProvinciaEntity;
import org.istrfa.repositories.DepartamentoRepository;
import org.istrfa.repositories.DistritoRepository;
import org.istrfa.repositories.ProvinciaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class UbigeoService {

    private final DepartamentoRepository departamentoRepository;
    private final ProvinciaRepository provinciaRepository;
    private final DistritoRepository distritoRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public UbigeoService(DepartamentoRepository departamentoRepository, ProvinciaRepository provinciaRepository, DistritoRepository distritoRepository, ModelMapper modelMapper) {
        this.departamentoRepository = departamentoRepository;
        this.provinciaRepository = provinciaRepository;
        this.distritoRepository = distritoRepository;
        this.modelMapper = modelMapper;
    }

    public List<DepartamentoDTO> getAllDepartament() {
        List<DepartamentoEntity> list = departamentoRepository.findAll();
        return list.stream().map(
                        x -> modelMapper.map(x, DepartamentoDTO.class))
                .collect(Collectors.toList());
    }

    public List<ProvinciaDTO> getProvincesByDepart(UUID idDepartament) {
        List<ProvinciaEntity> list = provinciaRepository.findByDepart(idDepartament);
        return list.stream().map(
                        x -> modelMapper.map(x, ProvinciaDTO.class))
                .collect(Collectors.toList());
    }

    public List<DistritoDTO> getDistritsByProvin(UUID idProvince) {
        List<DistritoEntity> list = distritoRepository.findByProvince(idProvince);
        return list.stream().map(
                        x -> modelMapper.map(x, DistritoDTO.class))
                .collect(Collectors.toList());
    }


//    public List<Integer> leerExcel(MultipartFile file) throws IOException {
////        List<Certificado> certificados = new ArrayList<>();
//
//        try (InputStream inputStream = file.getInputStream()) {
//            Workbook workbook = new XSSFWorkbook(inputStream);
//            String sheetName = "Registrados";
//            var sheet = workbook.getSheet(sheetName);
//            if (sheet == null) {
//                throw new IllegalArgumentException("Hoja con el nombre '" + sheetName + "' no encontrada");
//            }
//
//            for (var row : sheet) {
//                if (row.getRowNum() == 0) { // Saltar la primera fila (cabecera)
//                    continue;
//                }
////                Certificado certificado = new Certificado();
////                certificado.setNumeroregistro(row.getCell(0).getStringCellValue().trim());
////                if (row.getCell(1).getCellType() == CellType.NUMERIC) {
////                    certificado.setDocumento(String.valueOf((long) row.getCell(1).getNumericCellValue()).trim());
////                } else{
////                    certificado.setDocumento(row.getCell(1).getStringCellValue().trim());
////                }
////
////                certificado.setNombre(row.getCell(2).getStringCellValue().trim());
////                certificado.setApellido(row.getCell(3).getStringCellValue().trim());
////                certificado.setTipo(row.getCell(4).getStringCellValue().trim());
////
////                certificados.add(certificado);
//            }
//        }
//
//        return null;
//    }




}
