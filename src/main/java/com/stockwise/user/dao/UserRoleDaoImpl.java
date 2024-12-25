package com.stockwise.user.dao;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.stockwise.common.exception.CustomException;
import com.stockwise.user.entity.UserRole;
import com.stockwise.user.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRoleDaoImpl implements UserRoleDao{

    private final UserRoleRepository userRoleRepository;

    @Override
    public UserRole saveUserRole(UserRole userRole) {
        try {
            return userRoleRepository.save(userRole);
        } catch (Exception e) {
           throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.name(), e);
        }
    }
    
}
