package com.stockwise.user.dao;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.stockwise.common.emuns.EntityStatus;
import com.stockwise.common.exception.CustomException;
import com.stockwise.user.entity.User;
import com.stockwise.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final UserRepository userRepository;

    @Override
    public Mono<User> saveUser(User user) {
       try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new CustomException(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Mono<User> userByEmailAndIsEmailVerified(String email, Boolean isVerified) {
        return userRepository.findByEmailAndIsEmailVerifiedAndEntityStatus(email, isVerified, EntityStatus.ACTIVE);
    }
    
}
