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

import lombok.RequiredArgsConstructor;

import com.stockwise.common.exception.CustomException;
import com.stockwise.common.model.ProviderModel;
import com.stockwise.common.util.Mapper;
import com.stockwise.auth.enums.Provider;

@Component
@RequiredArgsConstructor
public class SsoJwtVerifier {
    
    @Qualifier("googleJwkSet")
	private final JWKSet googleJwkSet;

    private final Mapper mapper;


    public ProviderModel verifySsoProviderToken(String token, Provider provider) {
		JWTClaimsSet claimsSet = null;

		if (provider.equals(Provider.GOOGLE)) {

			claimsSet = verifyAndGetClaims(googleJwkSet, token);

        }
		return mapper.convert(claimsSet.getClaims(), ProviderModel.class);
	}

    private JWTClaimsSet verifyAndGetClaims(JWKSet jwkSet, String token) {
		try {
			SignedJWT jwt = SignedJWT.parse(token);

			RSASSAVerifier jwsVerifier = new RSASSAVerifier(
					jwkSet.getKeyByKeyId(jwt.getHeader().getKeyID()).toRSAKey());

			if (!jwt.verify(jwsVerifier)) {
				throw new CustomException("jwt signature verification failed",
						HttpStatus.BAD_REQUEST);
			}

			JWTClaimsSet claims = jwt.getJWTClaimsSet();

			if (claims.getExpirationTime().before(new Date())) {
				throw new CustomException("jwt token expired",
						HttpStatus.UNAUTHORIZED);
			}

			return claims;

		} catch (ParseException e) {
			throw new CustomException("jwt token invalid",
					HttpStatus.BAD_REQUEST);

		} catch (JOSEException e) {
			throw new CustomException("jwt signature verification failed",
					HttpStatus.BAD_REQUEST);

		} catch (CustomException e) {
			throw e;

		} catch (Exception e) {

			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
