package com.stockwise.common.service;

import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.stockwise.common.constant.AwsConstants;
import com.stockwise.common.exception.CustomException;
import com.stockwise.common.util.Mapper;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@Service
@RequiredArgsConstructor
public class SecretsManagerServiceimpl implements SecretsManagerService{

	private final Mapper mapper;

	private final Environment env;

	private Map<String, String> secretsMap;

    @Override
    public void loadSecrets() {
        try {
			SecretsManagerClient secretsClient = SecretsManagerClient.builder()
					.credentialsProvider(DefaultCredentialsProvider.create())
					.region(Region.of(env.getProperty(AwsConstants.SECRET_MANAGER_REGION))).build();

			GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
					.secretId(env.getProperty(AwsConstants.SECRET_ID)).build();

			GetSecretValueResponse valueResponse = secretsClient.getSecretValue(valueRequest);

			// log.info("Secrets retrieved successfully from AWS Secrets Manager.");

			secretsMap = mapper.convertStringToHashMap(valueResponse.secretString());

		} catch (Exception ex) {
			// log.error("Failed to load secrets from AWS Secrets Manager: {}", ex.getMessage(), ex);
			throw new CustomException("aws secrets loading failed",	HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

    @Override
    public String getSecret(String key) {
        if (secretsMap == null) {
			throw new CustomException("secrets not loaded",
					HttpStatus.NOT_FOUND);
		}

		String secret = secretsMap.get(key);
		if (secret == null) {
			throw new CustomException("secret key not found",
					HttpStatus.NOT_FOUND);
		}

		return secret;
    }
    
}
