package com.stockwise.auth.util;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.stockwise.auth.jwt.JwtVerifier;
import com.stockwise.common.constant.AuthConstant;
import com.stockwise.common.exception.CustomException;
import com.stockwise.common.model.AuthTokenModel;

import lombok.RequiredArgsConstructor;

/**
 * Utility class for generating JWT tokens.
 * 
 * @author Mindbowser
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

	private final Environment environment;

	@Qualifier("stockwiseJwkSet")
	private final JWKSet stockwiseJwkSet;

	/**
	 * Generates access and refresh tokens.
	 *
	 * @param subject     the subject of the token
	 * @param authorities the authorities associated with the token
	 * @return {@link AuthTokenModel}
	 */
	public AuthTokenModel generateToken(String subject, String authorities, String uuid) {

		String accessToken = generateSignedToken(subject, authorities, getAccessTokenExpiry(), uuid);
		String refreshToken = generateSignedToken(subject, null, getRefreshTokenExpiry(), uuid);

		return  new AuthTokenModel(accessToken, refreshToken, AuthConstant.BEARER.trim());
	}

	/**
	 * Generates a signed JWT token.
	 *
	 * @param subject     the subject of the token
	 * @param authorities the authorities associated with the token
	 * @param expiryMs    the token expiry time in milliseconds
	 * @return {@link String}
	 */
	private String generateSignedToken(String subject, String authorities, long expiryMs, String uuid) {
		JWTClaimsSet claimsSet = buildJwtClaims(subject, authorities, expiryMs, uuid);
		return signJwt(claimsSet);
	}

	/**
	 * Signs a JWT with the RSA private key.
	 *
	 * @param claimsSet the JWT claims set
	 * @return the serialized JWT token
	 */
	private String signJwt(JWTClaimsSet claimsSet) {
		try {
			RSAKey rsaKey = stockwiseJwkSet.getKeys().get(0).toRSAKey();
			String kid = rsaKey.getKeyID();

			SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(kid).build(),
					claimsSet);

			JWSSigner signer = new RSASSASigner(rsaKey);
			signedJWT.sign(signer);

			return signedJWT.serialize();
		} catch (JOSEException e) {
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Builds the JWT claims set.
	 *
	 * @param subject     the subject of the token
	 * @param authorities the authorities associated with the token
	 * @param expiryMs    the token expiry time in milliseconds
	 * @return the JWT claims set
	 */
	private JWTClaimsSet buildJwtClaims(String subject, String authorities, long expiryMs, String uuid) {
		return new JWTClaimsSet.Builder().subject(subject).issuer(AuthConstant.ISSUER)
				.claim(AuthConstant.AUTHORITIES, authorities).claim(AuthConstant.UUID, uuid)
				.expirationTime(new Date(System.currentTimeMillis() + expiryMs)).build();
	}

	/**
	 * Retrieves the access token expiry time from the environment.
	 *
	 * @return the access token expiry time in milliseconds
	 */
	private Long getAccessTokenExpiry() {
		return Long.parseLong(environment.getProperty(AuthConstant.ACCESS_TOKEN_EXPIRY_MS));
	}

	/**
	 * Retrieves the refresh token expiry time from the environment.
	 *
	 * @return the refresh token expiry time in milliseconds
	 */
	private Long getRefreshTokenExpiry() {
		return Long.parseLong(environment.getProperty(AuthConstant.REFRESH_TOKEN_EXPIRY_MS));
	}


	public static RSAKey loadRsaKeyFromJson(String filePath) throws IOException, ParseException {
    //    ObjectMapper objectMapper = new ObjectMapper();
    //     JsonNode rootNode = objectMapper.readTree(new File(filePath));

    //     // Parse the JSON into an RSA key
    //     RSAKey rsaKey = RSAKey.parse(rootNode.toString());
        
    //     return rsaKey;

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode = null;
    
    try {
        // Read the JSON file into a JsonNode
        rootNode = objectMapper.readTree(new File(filePath));
    } catch (IOException e) {
        Logger.getLogger(JwtVerifier.class.getName()).log(Level.SEVERE, "Error reading JSON file: " + filePath, e);
        throw new IOException("Error reading JWK JSON file", e);
    }

    // Ensure the rootNode contains the expected structure of a JWK
    if (rootNode == null || !rootNode.has("kty") || !rootNode.has("n")) {
        throw new ParseException("Invalid JWK structure: Missing key type or modulus", 0);
    }

    try {
        // Parse the root node into an RSAKey
        RSAKey rsaKey = RSAKey.parse(rootNode.toString());

        // Return the RSA key after successful parsing
        return rsaKey;
    } catch (ParseException e) {
        Logger.getLogger(JwtVerifier.class.getName()).log(Level.SEVERE, "Error parsing JWK JSON into RSAKey", e);
        throw new ParseException("Error parsing JWK JSON into RSAKey", 0);
    }
    }
}
