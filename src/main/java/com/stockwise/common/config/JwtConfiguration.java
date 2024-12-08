package com.stockwise.common.config;

import java.net.URI;
import java.net.URL;
import java.text.ParseException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;

import com.nimbusds.jose.jwk.JWKSet;
import com.stockwise.common.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JwtConfiguration {

    private final Environment env;

    @Bean("stockwiseJwkSet")
    JWKSet stockwiseJwkSet() {
        try {
            return JWKSet.parse(env.getProperty("stockwise.jwk.set.private.key"));
        } catch (ParseException e) {
            throw new IllegalStateException("Failed to load JWK set from secret manager and error message: {}", e);
        }
    }

    @Bean("googlwJwkSet")
    JWKSet googleJwkSet() {
        return fetchJwkByUrl("https://www.googleapis.com/oauth2/v3/certs", "unable to fetch google public jwk set");
    }

    private JWKSet fetchJwkByUrl(String url, String errorMessage) {
        try {
            URL uri = new URI(url).toURL();
            return JWKSet.load(uri);
        } catch (Exception ex) {
            throw new CustomException(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
