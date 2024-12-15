package com.stockwise.user.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenRequest {
    
    private String subject;

    private String authorities;

    private String userUuid;
}
