package com.stockwise.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthTokenModel {
    
    private String accessToken;

	private String refreshToken;

	private String tokenType;
}
