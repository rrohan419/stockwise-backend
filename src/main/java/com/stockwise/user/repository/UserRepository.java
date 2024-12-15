package com.stockwise.user.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.stockwise.common.emuns.EntityStatus;
import com.stockwise.user.entity.User;

import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long>{
    Mono<User> findByEmailAndIsEmailVerifiedAndEntityStatus(String email, Boolean isEmailVerified, EntityStatus entityStatus);

}
