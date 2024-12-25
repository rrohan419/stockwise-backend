package com.stockwise.user.dao;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.stockwise.common.exception.CustomException;
import com.stockwise.user.entity.Role;
import com.stockwise.user.enums.UserType;
import com.stockwise.user.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoleDaoImpl implements RoleDao{

    private final RoleRepository roleRepository;

    @Override
    public Role roleByUserType(UserType userType) {
        try {
            return roleRepository.findByUserType(userType);
        } catch (Exception e) {
            throw new CustomException("Internal Server Error", e);
        }
    }
    
}
