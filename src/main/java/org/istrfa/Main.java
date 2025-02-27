package org.istrfa;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
    }

    @PostConstruct
    public void init() {
        // Establece la zona horaria de Perú (America/Lima)
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper=new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("*").allowedHeaders("*");
            }
        };
    }
}