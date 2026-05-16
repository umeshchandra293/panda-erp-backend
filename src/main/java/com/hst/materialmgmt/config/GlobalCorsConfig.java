package com.hst.materialmgmt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GlobalCorsConfig {

    @Bean
    // This annotation is the magic bullet. It forces this filter to run BEFORE Spring Security.
    @Order(Ordered.HIGHEST_PRECEDENCE) 
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:5173"); // Your exact React port
        config.addAllowedHeader("*");
        config.addAllowedMethod("*"); // Allows GET, POST, PUT, DELETE, OPTIONS

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Apply to all endpoints

        return new CorsWebFilter(source);
    }
}
