package com.hst.materialmgmt.config;

import com.hst.materialmgmt.util.JwtUtil;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthFilter implements WebFilter {

    private static final List<String> PUBLIC = List.of(
        "/material/mgmt/auth/login",
        "/material/mgmt/auth/hash",
        "/v3/api-docs",
        "/swagger-ui",
        "/webjars",
        "/actuator"
    );

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest  req  = exchange.getRequest();
        ServerHttpResponse res  = exchange.getResponse();
        String             path = req.getPath().value();

        // ── 1. Handle OPTIONS preflight — return 200 immediately
        // (Actual CORS headers are now injected by CorsConfig)
        if (HttpMethod.OPTIONS.equals(req.getMethod())) {
            res.setStatusCode(HttpStatus.OK);
            return res.setComplete();
        }

        // ── 2. Allow public paths without JWT
        boolean isPublic = PUBLIC.stream().anyMatch(path::startsWith);
        if (isPublic) {
            return chain.filter(exchange);
        }

        // ── 3. Validate JWT
        String authHeader = req.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            res.setStatusCode(HttpStatus.UNAUTHORIZED);
            return res.setComplete();
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.isValid(token)) {
            res.setStatusCode(HttpStatus.UNAUTHORIZED);
            return res.setComplete();
        }
        return chain.filter(exchange);
    }
}