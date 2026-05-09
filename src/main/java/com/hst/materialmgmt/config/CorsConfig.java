package com.hst.materialmgmt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Global CORS configuration for the WebFlux application.
 *
 * Allows the React dev server (localhost:5173) and any origin configured via
 * the CORS_ALLOWED_ORIGINS environment variable to call all /api/** endpoints.
 *
 * This replaces the per-controller @CrossOrigin annotations (e.g. the one on
 * PurchaseOrderController) — remove those once this config is in place.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Origins: React dev server + any extra origins from env
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",   // Vite dev server
                "http://localhost:3000"    // CRA / alternative dev server
        ));

        // Allow all standard HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Allow all headers the browser might send
        config.setAllowedHeaders(List.of("*"));

        // Allow cookies / auth headers if you add auth later
        config.setAllowCredentials(true);

        // Cache preflight for 1 hour — reduces OPTIONS requests
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return new CorsWebFilter(source);
    }
}