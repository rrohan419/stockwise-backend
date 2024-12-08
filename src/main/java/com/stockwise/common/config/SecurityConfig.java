package com.stockwise.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.stockwise.common.filter.ReactiveAuthenticationFilter;
import com.stockwise.common.jwt.JwtVerifier;

@EnableReactiveMethodSecurity(useAuthorizationManager = true)
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtVerifier jwtVerifier;

    public SecurityConfig(JwtVerifier jwtVerifier) {
        this.jwtVerifier = jwtVerifier;
    }

    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http.
        cors(cors -> cors.disable()).csrf(csrf -> csrf.disable())
            .authorizeExchange(auth -> auth.pathMatchers("/**").permitAll().anyExchange().authenticated());
            
            http.addFilterBefore(new ReactiveAuthenticationFilter(jwtVerifier), SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();

    }
}
