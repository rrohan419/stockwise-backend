package com.stockwise.common.service;

public interface SecretsManagerService {
    
    void loadSecrets();

    String getSecret(String key);
}
