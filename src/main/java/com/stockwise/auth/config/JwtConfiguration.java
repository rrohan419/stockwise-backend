package com.stockwise.auth.config;

import java.text.ParseException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import com.nimbusds.jose.jwk.JWKSet;
import com.stockwise.common.constant.AwsConstants;
import com.stockwise.common.service.SecretsManagerService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@DependsOn("secretsManagerConfig")
public class JwtConfiguration {

    private final SecretsManagerService secretsManagerService;

    @Bean("stockwiseJwkSet")
    @Primary
    JWKSet stockwiseJwkSet() {
        JWKSet jwkSet = null;
        try {
            String jwkString = secretsManagerService.getSecret(AwsConstants.PRIVATE_JWK);
			jwkSet = JWKSet.parse(jwkString);
        } catch (ParseException e) {
            throw new IllegalStateException("Failed to load JWK set from secret manager and error message: {}", e);
        }
        return jwkSet;
    }
}
