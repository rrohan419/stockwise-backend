package com.stockwise.auth.config;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.stockwise.auth.filter.JwtValidationFilter;
import com.stockwise.auth.util.StockwiseJwtVerifier;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationWebFilter implements WebFilter {


    private final JwtValidationFilter jwtValidationFilter;

    public JwtAuthenticationWebFilter(JwtValidationFilter jwtValidationFilter) {
        this.jwtValidationFilter = jwtValidationFilter;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        if (path.startsWith("/public/")) {
            return chain.filter(exchange); // Skip JWT processing for public endpoints
        }
        // Proceed with JWT validation for other paths
        return jwtValidationFilter.validateJwt(exchange, chain);
    }
}

