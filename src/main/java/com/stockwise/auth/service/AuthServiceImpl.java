package com.stockwise.auth.service;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.stockwise.common.constant.AwsConstants;
import com.stockwise.common.service.SecretsManagerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final Environment env;
    private final SecretsManagerService secretManagerService;
    
    @Override
    public String publicJwkJson() {
        return secretManagerService.getSecret(AwsConstants.PUBLIC_JWK);
    }
}
