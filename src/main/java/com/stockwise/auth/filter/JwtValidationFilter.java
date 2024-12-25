package com.stockwise.auth.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import com.nimbusds.jwt.SignedJWT;
import com.stockwise.auth.util.StockwiseJwtVerifier;

import java.text.ParseException;

@Component
public class JwtValidationFilter {

    private final StockwiseJwtVerifier stockwiseJwtVerifier; // Your JWT verifier component

    public JwtValidationFilter(StockwiseJwtVerifier stockwiseJwtVerifier) {
        this.stockwiseJwtVerifier = stockwiseJwtVerifier;
    }

    public Mono<Void> validateJwt(ServerWebExchange exchange, WebFilterChain chain) {
        String token = extractJwtFromRequest(exchange);
        if (token == null || token.isEmpty()) {
            // No token provided; proceed without validation
            return chain.filter(exchange);
        }

        try {
            // Validate and get claims
            stockwiseJwtVerifier.verifyAndGetClaims(token);
            // Token is valid; proceed with request
            return chain.filter(exchange);
        } catch (Exception e) {
            // Handle invalid token
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private String extractJwtFromRequest(ServerWebExchange exchange) {
        String bearerToken = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}

