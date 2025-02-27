package org.istrfa.services;

import lombok.extern.slf4j.Slf4j;
import org.istrfa.dto.ApiPeruDni;
import org.istrfa.dto.ApiPeruRuc;
import org.istrfa.utils.Constantes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ApiPeruService {

    @Value("${services.api-peru}")
    private String api_peru;
    private final RestTemplate restTemplate;

    @Autowired
    public ApiPeruService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public ApiPeruDni findByDNI(String numDNI) {
        try {
            String url = api_peru + "/dni/" + numDNI + "?token=" + Constantes.TOKEN_API_PERU;
            ResponseEntity<ApiPeruDni> response =
                    restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, ApiPeruDni.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error al buscar por dni en el servicio de apis: " + e);
            return null;
        }
    }

    public ApiPeruRuc findByRUC(String numRUC) {
        try {
            String url = api_peru + "/ruc/" + numRUC + "?token=" + Constantes.TOKEN_API_PERU;
            ResponseEntity<ApiPeruRuc> response =
                    restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, ApiPeruRuc.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error al buscar por dni en el servicio de apis: " + e);
            return null;
        }
    }

}
