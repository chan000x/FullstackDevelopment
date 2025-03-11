package com.chandana.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("#{'${cors.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;

    @Value("#{'${cors.allowed-methods}'.split(',')}")
    private List<String> allowedMethods;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Apply CORS for "/api/**" paths
        registry.addMapping("/api/**")
                .allowedOrigins(getAllowedOrigins())
                .allowedMethods(getAllowedMethods())
                .allowedHeaders("*") // Allows all headers
                .allowCredentials(true); // Allow credentials
    }

    // Helper method to handle wildcard origins
    private String[] getAllowedOrigins() {
        if (allowedOrigins.contains("*")) {
            return new String[]{"*"};
        }
        return allowedOrigins.toArray(new String[0]);
    }

    // Helper method to handle wildcard methods
    private String[] getAllowedMethods() {
        if (allowedMethods.contains("*")) {
            return new String[]{"GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"};
        }
        return allowedMethods.toArray(new String[0]);
    }
}
