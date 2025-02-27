package org.istrfa.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;



@Service
@Slf4j
public class ApiBillingService {

    @Value("${services.api-billing}")
    private String api;
    private final RestTemplate restTemplate;

    @Autowired
    public ApiBillingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


}
