package com.hst.materialmgmt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${CORS_ALLOWED_ORIGINS:}")
    private String extraOrigins;

    @Bean
    @Order(-100) // Ensures CORS is handled before Spring Security / JWT filters
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = new ArrayList<>(List.of(
            "http://localhost:5173",
            "http://localhost:3000",
            "https://panda-frontend-zdnv.vercel.app"
        ));
        if (extraOrigins != null && !extraOrigins.isBlank()) {
            for (String o : extraOrigins.split(",")) {
                if (!o.isBlank()) origins.add(o.trim());
            }
        }
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}