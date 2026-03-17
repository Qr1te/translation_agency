package com.qritiooo.translationagency.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI translationAgencyOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Translation Agency API")
                .description("REST API for managing clients, translators, documents and orders")
                .version("v1")
                .contact(new Contact().name("Translation Agency"))
                .license(new License().name("Internal use")));
    }
}
