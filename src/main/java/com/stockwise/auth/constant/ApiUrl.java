package com.stockwise.auth.constant;

public class ApiUrl {
    private ApiUrl() {
	}

    public static final String BASE_URL = "/v1";
    public static final String PUBLIC_BASE_URL = "/public/v1";

    public static final String SOCIAL_SIGNIN = "/social-sso";
    public static final String PUBLIC_JWK_JSON = "/.well-known/jwks.json";
}
