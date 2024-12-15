package com.stockwise.user.service;

import com.stockwise.common.model.AuthTokenModel;
import com.stockwise.user.dto.SocialSigningDto;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

public interface UserService {
    
    Mono<AuthTokenModel> socialSigning(@Valid SocialSigningDto signingDto);
}
