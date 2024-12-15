package com.stockwise.user.service;

import com.stockwise.common.model.AuthTokenModel;
import com.stockwise.user.dto.SocialSigningDto;

import jakarta.validation.Valid;

public interface UserService {
    
    AuthTokenModel socialSigning(@Valid SocialSigningDto signingDto);
}
