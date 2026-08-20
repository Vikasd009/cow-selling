package com.vikas.cowselling.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cowSellingOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title(
                                        "Domestic Cow Selling API"
                                )
                                .version("1.0.0")
                                .description(
                                        "Backend API for buying and selling domestic cows"
                                )
                                .contact(
                                        new Contact()
                                                .name("Vikas D")
                                )
                )
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearerAuth")
                )
                .components(
                        new io.swagger.v3.oas.models.Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .name("bearerAuth")
                                                .type(
                                                        SecurityScheme.Type.HTTP
                                                )
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }
}

