package com.stockwise.user.dao;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.stockwise.user.entity.UserIdentity;
import com.stockwise.user.repository.UserIdentityRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserIdentityDaoImpl implements UserIdentityDao {

    private final UserIdentityRepository userIdentityRepository;

    @Override
    public Optional<UserIdentity> optionalUserIdentity(String providerId) {
        return userIdentityRepository.findByProviderId(providerId);
    }

    @Override
    public UserIdentity saveUserIdentity(UserIdentity userIdentity) {
        return userIdentityRepository.save(userIdentity);
    }
    
}
