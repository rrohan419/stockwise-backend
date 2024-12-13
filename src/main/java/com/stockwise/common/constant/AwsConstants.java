package com.stockwise.common.constant;

public class AwsConstants {
    
    private AwsConstants() {
	}

	public static final String SECRET_ID = "secrets.manager.secret.id";
	public static final String SECRET_MANAGER_REGION = "secrets.manager.region";

	 public static final String PRIVATE_JWK = "privateJwk";
	public static final String PUBLIC_JWK = "publicJwk";
	
	// internal http call sceret key
	public static final String INTERNAL_HTTP_SECRET_KEY = "internalApiCallSecretKey";
}
