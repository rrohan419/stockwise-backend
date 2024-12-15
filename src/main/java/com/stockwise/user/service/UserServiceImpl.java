package com.stockwise.user.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.stockwise.auth.enums.Provider;
import com.stockwise.auth.service.AuthService;
import com.stockwise.auth.util.JwtUtil;
import com.stockwise.common.model.AuthTokenModel;
import com.stockwise.common.model.ProviderModel;
import com.stockwise.user.dao.UserDao;
import com.stockwise.user.dao.UserIdentityDao;
import com.stockwise.user.dto.SocialSigningDto;
import com.stockwise.user.entity.User;
import com.stockwise.user.entity.UserIdentity;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthService authService;
    private final UserIdentityDao userIdentityDao;
    private final UserDao userDao;
    private final JwtUtil jwtUtil;

    @Override
    public Mono<AuthTokenModel> socialSigning(@Valid SocialSigningDto signingDto) {
        return authService.socialSigning(signingDto)
                .flatMap(providerModel -> {
                    // Extract providerId based on the provider type
                    String providerId = getProviderId(signingDto.getProvider(), providerModel);

                    // Find user by providerId
                    return userIdentityDao.userIdentityByProvider(providerId)
                            .flatMap(userIdentity -> jwtUtil.generateToken(userIdentity.getProviderId(),
                                    userIdentity.getUser().getCommaSeparatedRoles(), userIdentity.getUser().getUuid()))
                            .switchIfEmpty(
                                    handleNewSocialUser(providerId, providerModel.getEmail(), providerModel.getName(),
                                            signingDto.getProvider()));
                });

    }

    private String getProviderId(Provider provider, ProviderModel providerModel) {
        if (provider.equals(Provider.GOOGLE)) {
            return providerModel.getSub();
        }
        // Handle other providers if needed
        throw new IllegalArgumentException("Unsupported provider: " + provider);
    }

    private Mono<AuthTokenModel> handleNewSocialUser(String providerId, String email, String name, Provider provider) {
        return Mono.defer(() -> {
            // Check if a user exists by email
            return userDao.userByEmailAndIsEmailVerified(email, Boolean.TRUE)
                    .switchIfEmpty(
                            // If user doesn't exist, create a new user
                            Mono.defer(() -> {
                                User newUser = buildUserBySocials(email, name);
                                return userDao.saveUser(newUser);
                            }))
                    .flatMap(user -> {
                        // Create a new UserIdentity for the user
                        UserIdentity userIdentity = buildUserIdentity(user, providerId, provider);
                        return userIdentityDao.saveUserIdentity(userIdentity)
                                .then(jwtUtil.generateToken(userIdentity.getProviderId(),
                                userIdentity.getUser().getCommaSeparatedRoles(), userIdentity.getUser().getUuid()));
                    });
        });
    }

    private UserIdentity buildUserIdentity(User user, String providerId, Provider provider) {
        return new UserIdentity(UUID.randomUUID().toString(), null, provider, providerId, user.getEmail(),
                user.getPhoneCountryCode() + user.getPhoneNumber(), user);
    }

    private User buildUserBySocials(String email, String name) {
        User user = new User();
        user.setUuid(UUID.randomUUID().toString());

        if (email != null && !email.isBlank()) {
            user.setEmail(email);
            user.setIsEmailVerified(true);
        }

        if (name != null && !name.isBlank()) {
            String[] nameParts = name.split(" ", 2);
            user.setFirstName(nameParts[0]);

            if (nameParts.length > 1) {
                user.setLastName(nameParts[1]);
            } else {
                user.setLastName("");
            }
        }

        return user;
    }
}
