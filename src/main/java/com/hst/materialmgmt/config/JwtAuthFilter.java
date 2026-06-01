package com.hst.materialmgmt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import com.hst.materialmgmt.util.JwtUtil;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements WebFilter {

    @Autowired private JwtUtil jwt;

    // Paths that don't need a token
    private static final String[] PUBLIC = {
        "/material/mgmt/auth/login",
        "/material/mgmt/auth/hash",
        "/v3/api-docs",
        "/swagger-ui",
        "/webjars",
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // Allow public paths
        for (String pub : PUBLIC) {
            if (path.startsWith(pub)) return chain.filter(exchange);
        }
        // Allow OPTIONS (CORS preflight)
        if (exchange.getRequest().getMethod().name().equals("OPTIONS"))
            return chain.filter(exchange);

        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwt.isValid(token)) {
                return chain.filter(exchange);
            }
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}