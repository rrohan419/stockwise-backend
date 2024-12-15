package com.stockwise.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.stockwise.common.emuns.EntityStatus;
import com.stockwise.user.entity.User;


public interface UserRepository extends JpaRepository<User, Long>{
    User findByEmailAndIsEmailVerifiedAndEntityStatus(String email, Boolean isEmailVerified, EntityStatus entityStatus);

}
