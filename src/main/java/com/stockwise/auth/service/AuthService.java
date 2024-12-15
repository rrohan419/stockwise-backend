package com.stockwise.auth.service;

import com.stockwise.common.model.ProviderModel;
import com.stockwise.user.dto.SocialSigningDto;

import jakarta.validation.Valid;

public interface AuthService {
    
    String publicJwkJson();

    ProviderModel socialSigning(@Valid SocialSigningDto signingDto);
}
