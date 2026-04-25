package com.bank.Hackathon_Java6.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Favorite Payee API")
                        .version("1.0.0")
                        .description("HCLTech Digital Business Capability - Favorite Payee Use Case")
                        .contact(new Contact().name("HCLTech").url("https://www.hcltech.com")));
    }
}

