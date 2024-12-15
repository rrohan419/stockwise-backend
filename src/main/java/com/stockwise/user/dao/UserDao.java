package com.stockwise.user.dao;

import com.stockwise.user.entity.User;

import reactor.core.publisher.Mono;

public interface UserDao {

    Mono<User> saveUser(User user);

    Mono<User> userByEmailAndIsEmailVerified(String email, Boolean isVerified);
}
