package com.stockwise.common.config;

import org.springframework.context.annotation.Configuration;

import com.stockwise.common.service.SecretsManagerService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecretsManagerConfig {
    
    private final SecretsManagerService secretsManagerService;


    @PostConstruct
    public void init(){
        secretsManagerService.loadSecrets();
    }
}
