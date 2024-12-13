package com.stockwise.auth.jwt;

import java.text.ParseException;
import java.util.Date;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.stockwise.common.constant.AppConstant;
import com.stockwise.common.exception.CustomException;

@Component
// @ConditionalOnProperty(name = AppConstant.JWT_CONFIG_ENABLED, havingValue = AppConstant.TRUE)
public class JwtVerifier {
    
    private final JWKSet jwkSet;

    JwtVerifier(JWKSet jwkSet) {
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

			if (!"stockwise".equals(claims.getIssuer())) {
				throw new CustomException("Invalid JWT issuer", HttpStatus.UNAUTHORIZED);
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
			throw new CustomException("Unexpected error occurred while verifying JWT", e);
		}
	}
}
