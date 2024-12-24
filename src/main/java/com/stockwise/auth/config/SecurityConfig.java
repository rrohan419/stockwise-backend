package com.stockwise.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;

import com.stockwise.auth.filter.JwtValidationFilter;
import com.stockwise.auth.util.StockwiseJwtVerifier;

import reactor.core.publisher.Mono;

// @EnableReactiveMethodSecurity(useAuthorizationManager = true)
@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    // private final SecurityContextRepository securityContextRepository;

    // public SecurityConfig(SecurityContextRepository securityContextRepository) {
    // this.securityContextRepository = securityContextRepository;
    // }

    // @Bean
    // SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http,
    // ReactiveAuthenticationManager authenticationManager,
    // ServerAuthenticationConverter authenticationConverter) {

    // return http
    // .csrf(ServerHttpSecurity.CsrfSpec::disable)
    // .cors(ServerHttpSecurity.CorsSpec::disable)
    // .securityContextRepository(securityContextRepository)
    // .authorizeExchange(exchange -> exchange
    // .pathMatchers("/public/**").permitAll()
    // .anyExchange().authenticated())
    // .build();

    // }

    // private final JwtAuthenticationConverter jwtAuthenticationConverter;

    // public SecurityConfig(JwtAuthenticationConverter jwtAuthenticationConverter)
    // {
    // this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    // }

    private final StockwiseJwtVerifier stockwiseJwtVerifier;

    public SecurityConfig(StockwiseJwtVerifier stockwiseJwtVerifier) {
        this.stockwiseJwtVerifier = stockwiseJwtVerifier;
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        // return http
        // .csrf().disable()
        // .authorizeExchange(exchanges -> exchanges
        // .pathMatchers("/public/**").permitAll()
        // .anyExchange().authenticated()
        // )
        // .addFilterAt(authenticationWebFilter(),
        // SecurityWebFiltersOrder.AUTHENTICATION)
        // .build();

        // return http
        // .csrf(ServerHttpSecurity.CsrfSpec::disable)
        // .authorizeExchange(exchange -> exchange
        // .pathMatchers("/public/**").permitAll()
        // .anyExchange().authenticated()
        // )
        // .addFilterBefore(new JwtAuthenticationWebFilter(new
        // JwtValidationFilter(stockwiseJwtVerifier)),
        // SecurityWebFiltersOrder.AUTHENTICATION)
        // .build();

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers("/public/**").permitAll() // Ensure public paths are not processed by JWT filter
                        .anyExchange().authenticated())
                .addFilterBefore(new JwtAuthenticationWebFilter(new JwtValidationFilter(stockwiseJwtVerifier)),
                        SecurityWebFiltersOrder.AUTHENTICATION) // Add the filter after authorization rules
                .build();
    }

    // private AuthenticationWebFilter authenticationWebFilter() {
    // AuthenticationWebFilter authenticationWebFilter = new
    // AuthenticationWebFilter(authenticationManager());
    // authenticationWebFilter.setServerAuthenticationConverter(jwtAuthenticationConverter);
    // return authenticationWebFilter;
    // }

    // @Bean
    // public ReactiveAuthenticationManager authenticationManager() {
    // return authentication -> {
    // // Already authenticated in JwtAuthenticationConverter
    // return Mono.just(authentication);
    // };
    // }
}
