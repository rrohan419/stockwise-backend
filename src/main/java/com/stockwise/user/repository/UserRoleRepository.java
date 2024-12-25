package com.stockwise.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stockwise.user.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long>{
    
}
