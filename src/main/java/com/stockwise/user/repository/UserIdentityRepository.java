package com.stockwise.user.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.stockwise.user.entity.UserIdentity;

import reactor.core.publisher.Mono;

public interface UserIdentityRepository extends ReactiveCrudRepository<UserIdentity, Long>{
    Mono<UserIdentity> findByProviderId(String providerId);
}
