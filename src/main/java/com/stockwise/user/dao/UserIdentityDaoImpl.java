package com.stockwise.user.dao;

import org.springframework.stereotype.Repository;

import com.stockwise.user.entity.UserIdentity;
import com.stockwise.user.repository.UserIdentityRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserIdentityDaoImpl implements UserIdentityDao {

    private final UserIdentityRepository userIdentityRepository;

    @Override
    public Mono<UserIdentity> userIdentityByProvider(String providerId) {
        return userIdentityRepository.findByProviderId(providerId);
    }

    @Override
    public Mono<UserIdentity> saveUserIdentity(UserIdentity userIdentity) {
        return userIdentityRepository.save(userIdentity);
    }
    
}
