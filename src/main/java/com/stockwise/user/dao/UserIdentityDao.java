package com.stockwise.user.dao;


import java.util.Optional;

import com.stockwise.user.entity.UserIdentity;

public interface UserIdentityDao {
    
    Optional<UserIdentity> optionalUserIdentity(String providerId);

    UserIdentity saveUserIdentity(UserIdentity userIdentity);
}
