package com.stockwise.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stockwise.user.entity.UserIdentity;


public interface UserIdentityRepository extends JpaRepository<UserIdentity, Long>{
    Optional<UserIdentity> findByProviderId(String providerId);
}
