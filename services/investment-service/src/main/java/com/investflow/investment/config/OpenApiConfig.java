package com.investflow.investment.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI investmentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("InvestFlow Investment & SIP Service API")
                        .description("REST API for managing trades, stocks, mutual funds, and automated SIPs")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("InvestFlow Engineering")
                                .email("engineering@investflow.internal")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
