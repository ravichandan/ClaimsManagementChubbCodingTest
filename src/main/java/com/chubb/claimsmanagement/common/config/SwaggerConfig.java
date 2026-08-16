package com.chubb.claimsmanagement.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.List;

@Configuration
/** Configures the generated OpenAPI metadata and shared JSON mapper. */
public class SwaggerConfig {

    @Bean
        /** Creates the JSON mapper used by Spring MVC and event serialization. */
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json().build();
    }

    @Bean
        /** Defines API title, contact, license, and server metadata. */
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Claims Management System API")
                        .description("API specification for the Claims Management System, a Spring Boot application for managing insurance claims.")
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@chubb.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development server"),
                        new Server()
                                .url("https://api.claimsmanagement.com")
                                .description("Production server")
                ));
    }
}
