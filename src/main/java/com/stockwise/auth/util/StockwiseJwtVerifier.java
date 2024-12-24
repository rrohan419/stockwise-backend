package com.stockwise.auth.util;

import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.stockwise.common.constant.AuthConstant;
import com.stockwise.common.exception.CustomException;

@Component
public class StockwiseJwtVerifier {
    
    private final JWKSet jwkSet;


    public StockwiseJwtVerifier(@Qualifier("stockwiseJwkSet") JWKSet jwkSet) {
		this.jwkSet = jwkSet;
	}

	public JWTClaimsSet verifyAndGetClaims(String token) {
		try {
			// Parse the JWT token
			SignedJWT jwt = SignedJWT.parse(token);

			RSASSAVerifier jwsVerifier = new RSASSAVerifier(
					jwkSet.getKeyByKeyId(jwt.getHeader().getKeyID()).toRSAKey());

			// Verify the JWT signature
			if (!jwt.verify(jwsVerifier)) {
				throw new CustomException("JWT signature verification failed", HttpStatus.INTERNAL_SERVER_ERROR);
			}

			// Get the claims from the JWT
			JWTClaimsSet claims = jwt.getJWTClaimsSet();

			// Check if the JWT is expired
			if (claims.getExpirationTime().before(new Date())) {
				throw new CustomException("JWT is expired", HttpStatus.UNAUTHORIZED);
			}
			
			// Check the JWT issuer
			if (claims.getIssuer() != null && !claims.getIssuer().isBlank()
					&& !claims.getIssuer().equals(AuthConstant.ISSUER)) {
				throw new CustomException("The token issuer is invalid", HttpStatus.BAD_REQUEST);
			}

			return claims;

		} catch (ParseException e) {
			// Handle errors related to JWT parsing
			throw new CustomException("Error parsing JWT token", HttpStatus.BAD_REQUEST);

		} catch (JOSEException e) {
			// Handle errors related to JOSE (JWT signature verification)
			throw new CustomException("Error verifying JWT signature", HttpStatus.BAD_REQUEST);

		} catch (CustomException e) {
			// Re-throw custom exceptions
			throw e;

		} catch (Exception e) {
			// Catch-all for any other unexpected exceptions
			throw new CustomException("Unexpected error occurred while verifying JWT. detail : "+e.getMessage(), e);
		}
	}
}
