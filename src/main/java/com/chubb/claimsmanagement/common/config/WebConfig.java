package com.chubb.claimsmanagement.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Configures browser access for the local Angular development client. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** Allows the frontend development server to call the versioned REST API. */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Development-only setting. This line will be removed before production and allow only approved origins.
        registry.addMapping("/api/**")
            .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
