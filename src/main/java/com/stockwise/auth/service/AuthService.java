package com.stockwise.auth.service;

import com.stockwise.common.model.ProviderModel;
import com.stockwise.user.dto.SocialSigningDto;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

public interface AuthService {
    
    String publicJwkJson();

    Mono<ProviderModel> socialSigning(@Valid SocialSigningDto signingDto);
}
