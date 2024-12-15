package com.stockwise.user.dao;


import com.stockwise.user.entity.UserIdentity;

import reactor.core.publisher.Mono;

public interface UserIdentityDao {
    
    Mono<UserIdentity> userIdentityByProvider(String providerId);

    Mono<UserIdentity> saveUserIdentity(UserIdentity userIdentity);
}
