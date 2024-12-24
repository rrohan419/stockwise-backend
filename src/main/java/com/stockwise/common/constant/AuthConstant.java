package com.stockwise.common.constant;

public class AuthConstant {
    
    private AuthConstant(){}

    public static final String BEARER = "Bearer ";
    public static final String AUTHORITIES = "authorities";
    public static final String UUID = "uuid";
    public static final String ISSUER = "STOCKWISE";
    public static final String AUTHORIZATION = "Authorization";

    public static final String ACCESS_TOKEN_EXPIRY_MS = "access.token.expiry.in.ms";
	public static final String REFRESH_TOKEN_EXPIRY_MS = "refresh.token.expiry.in.ms";

	public static final String ROLE_PREFIX = "ROLE_";
}
