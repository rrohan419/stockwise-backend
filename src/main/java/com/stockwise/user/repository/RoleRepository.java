package com.stockwise.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stockwise.user.entity.Role;
import com.stockwise.user.enums.UserType;

public interface RoleRepository extends JpaRepository<Role, Long>{
    
    Role findByUserType(UserType userType);
}
