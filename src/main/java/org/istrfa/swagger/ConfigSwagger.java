package org.istrfa.swagger;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

/**
 * The type Config swagger.
 */
@Configuration
@EnableSwagger2
public class ConfigSwagger {

    /**
     * Api docket.
     *
     * @param typeResolver the type resolver
     * @return the docket
     */
    @Bean
    public Docket api(TypeResolver typeResolver) {
        return new Docket(DocumentationType.SWAGGER_2)
                .protocols(Collections.singleton("https"))
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.istrfa"))
                .paths(PathSelectors.any())
                .build();
    }


}
