package com.stockwise.auth.service;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.stockwise.auth.enums.Provider;
import com.stockwise.auth.util.SsoJwtVerifier;
import com.stockwise.common.constant.AwsConstants;
import com.stockwise.common.exception.CustomException;
import com.stockwise.common.model.ProviderModel;
import com.stockwise.common.service.SecretsManagerService;
import com.stockwise.user.dto.SocialSigningDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final Environment env;
    private final SecretsManagerService secretManagerService;
    private final SsoJwtVerifier ssoJwtVerifier;
    
    @Override
    public String publicJwkJson() {
        return secretManagerService.getSecret(AwsConstants.PUBLIC_JWK);
    }

    @Override
    public ProviderModel socialSigning(@Valid SocialSigningDto signingDto) {
        Provider provider = signingDto.getProvider();
    
        return switch (provider) {
            case GOOGLE -> handleGoogleSignIn(signingDto);
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
    }
    
    private ProviderModel handleGoogleSignIn(SocialSigningDto signingDto) {
        // Validate the Google token and return the AuthTokenModel
        return validateGoogleToken(ssoJwtVerifier.verifySsoProviderToken(signingDto.getToken(), signingDto.getProvider()));
    }

    private ProviderModel validateGoogleToken(ProviderModel providerModel) {
		validateIssuer(providerModel, "https://accounts.google.com");
		return providerModel;
	}

    private void validateIssuer(ProviderModel providerModel, String expectedIssuer) {
		if (!expectedIssuer.equals(providerModel.getIss())) {
			throw new CustomException("Issuer didn't match",
					HttpStatus.UNAUTHORIZED);
		}
	}
}
