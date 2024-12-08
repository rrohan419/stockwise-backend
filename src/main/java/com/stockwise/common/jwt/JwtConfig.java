package com.stockwise.common.jwt;

import java.text.ParseException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.nimbusds.jose.jwk.JWKSet;
import com.stockwise.common.constant.AppConstant;
import com.stockwise.common.exception.CustomException;

@Configuration
@ConditionalOnProperty(name = AppConstant.JWT_CONFIG_ENABLED, havingValue = AppConstant.TRUE)
public class JwtConfig {
    
    private final WebClient webClient;
    private final Environment env;

    public JwtConfig(WebClient.Builder webClientBuilder, Environment environment) {
		this.webClient = webClientBuilder.build();
		this.env = environment;
	}

    @Bean
	public JWKSet createJWSSet() {

		try {
			// Fetch the JWK set URL and key ID from environment properties
			String jwkSetUrl = env.getProperty(AppConstant.JWT_CONFIG_JWK_URL);

			// Validate the JWK set URL and key ID
			if (jwkSetUrl == null || jwkSetUrl.isEmpty()) {
				throw new CustomException("JWK set URL is not configured or is empty", HttpStatus.BAD_REQUEST);
			}

			// Fetch the JWK Set string from the URL
			String jwkSetString = webClient.get().uri(jwkSetUrl).retrieve().bodyToMono(String.class).block();

			// Check if the JWK set string is null or empty
			if (jwkSetString == null || jwkSetString.isEmpty()) {
				throw new CustomException("Received an empty JWK set string from the server", HttpStatus.BAD_REQUEST);
			}

			// Parse the JWK Set string into a JWKSet object
			return JWKSet.parse(jwkSetString);

		} catch (WebClientResponseException e) {
			// Handle HTTP response errors (e.g., 4xx and 5xx status codes)
			throw new CustomException("HTTP error while fetching JWK set: " + e.getStatusCode(), e);

		} catch (WebClientRequestException e) {
			// Handle request-related errors (e.g., connection issues)
			throw new CustomException("Request error while fetching JWK set", e);

		} catch (ParseException e) {
			// Handle errors that occur during the parsing of the JWK set
			throw new CustomException("Error parsing JWK set", e);

		} catch (CustomException e) {
			// Re-throw custom exceptions
			throw e;

		} catch (Exception e) {
			// Catch-all for any other unexpected exceptions
			throw new CustomException("Unexpected error occurred while fetching or creating JWS verifier", e);
		}
	}
}
