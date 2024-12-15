package com.stockwise.user.dao;

import com.stockwise.user.entity.User;

public interface UserDao {

    User saveUser(User user);

    User userByEmailAndIsEmailVerified(String email, Boolean isVerified);
}
