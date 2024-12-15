package com.stockwise.user.dto;

import com.stockwise.auth.enums.Provider;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SocialSigningDto {
    
    @NotBlank
    private String token;

    @NotNull
    private Provider provider;
}
