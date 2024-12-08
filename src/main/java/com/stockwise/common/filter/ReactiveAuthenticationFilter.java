package com.stockwise.common.filter;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.stockwise.common.constant.AppConstant;
import com.stockwise.common.exception.CustomException;
import com.stockwise.common.jwt.JwtVerifier;

import reactor.core.publisher.Mono;

@Component
public class ReactiveAuthenticationFilter implements WebFilter {

    private final Logger log = LogManager.getLogger();

    private final JwtVerifier jwtVerifier;

    public ReactiveAuthenticationFilter(JwtVerifier jwtVerifier) {
        this.jwtVerifier = jwtVerifier;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(AppConstant.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            Mono.fromCallable(() -> jwtVerifier.verifyAndGetClaims(token))
                    .flatMap(claims -> {
                        String userUuid = claims.getSubject();

                        // Extract roles from the claims (e.g., assuming "roles" is the key)
                        List<String> roles;
                        try {
                            roles = Optional.ofNullable(claims.getStringListClaim("roles"))
                                    .orElse(Collections.emptyList());
                        } catch (ParseException e) {
                            throw new CustomException("error while extracting roles", HttpStatus.INTERNAL_SERVER_ERROR);
                        }

                        // Convert roles to SimpleGrantedAuthority
                        List<SimpleGrantedAuthority> authorities = roles.stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                                .toList();

                        // Create the authentication object
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userUuid, null, authorities);

                        // ReactiveSecurityContextHolder.withAuthentication(authentication);
                        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
                        return chain.filter(exchange)
                                .contextWrite(
                                        ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

                    });
        }

        // If no authorization header is found, continue the chain without setting
        // authentication
        return chain.filter(exchange);
    }

}
