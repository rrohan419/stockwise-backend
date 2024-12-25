package com.stockwise.user.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.stockwise.auth.enums.Provider;
import com.stockwise.auth.service.AuthService;
import com.stockwise.auth.util.JwtUtil;
import com.stockwise.common.constant.ExceptionMessage;
import com.stockwise.common.exception.CustomException;
import com.stockwise.common.model.AuthTokenModel;
import com.stockwise.common.model.ProviderModel;
import com.stockwise.user.dao.RoleDao;
import com.stockwise.user.dao.UserDao;
import com.stockwise.user.dao.UserIdentityDao;
import com.stockwise.user.dao.UserRoleDao;
import com.stockwise.user.dto.SocialSigningDto;
import com.stockwise.user.entity.Role;
import com.stockwise.user.entity.User;
import com.stockwise.user.entity.UserIdentity;
import com.stockwise.user.entity.UserRole;
import com.stockwise.user.enums.UserType;
import com.stockwise.user.model.TokenRequest;

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
    private final RoleDao roleDao;
    private final UserRoleDao userRoleDao;
    private final Environment env;

    @Override
    public AuthTokenModel socialSigning(@Valid SocialSigningDto signingDto) {
        ProviderModel providerModel = authService.socialSigning(signingDto);

        String providerId = null;

		if (signingDto.getProvider().equals(Provider.GOOGLE)) {
			providerId = providerModel.getSub();
		}

		Optional<UserIdentity> optionalUserIdentity = userIdentityDao.optionalUserIdentity(providerId);
		User user;

		if (optionalUserIdentity.isPresent()) {
			user = optionalUserIdentity.get().getUser();
		} else {
			String email = providerModel.getEmail();
			user = (email != null) ? userDao.userByEmailAndIsEmailVerified(email, Boolean.TRUE) : null;
            boolean isNewUser = false;
			if (user == null) {
				user = buildUserBySocials(email, providerModel.getName());
                
				UserIdentity userIdentity = buildUserIdentity(user, null, signingDto.getProvider(), providerId,
                providerModel.getEmail(), providerModel.getPicture());
				user.setUserIdentities(Arrays.asList(userIdentity));
                isNewUser = true;
			} else {
				List<UserIdentity> userIdentities = user.getUserIdentities();
				UserIdentity userIdentity = buildUserIdentity(user, null, signingDto.getProvider(), providerId,
                providerModel.getEmail(), providerModel.getPicture());
				userIdentities.add(userIdentity);
				user.setUserIdentities(userIdentities);
			}

			user = userDao.saveUser(user);
            if(isNewUser) {
                user = addUserRole(UserType.USER, user);
            }
             
		}

        TokenRequest builderModel = buildTokenRequest(user, signingDto.getProvider());
		AuthTokenModel authToken = jwtUtil.generateToken(builderModel.getSubject(), builderModel.getAuthorities(), builderModel.getUserUuid());
		return authToken;

    }

    private User addUserRole(UserType userType, User user) {
        Role role = roleDao.roleByUserType(userType);

        UserRole userRole = new UserRole(role,user);
        userRole = userRoleDao.saveUserRole(userRole);

        user.setUserRoles(List.of(userRole));
        return userDao.saveUser(user);
    }

    private TokenRequest buildTokenRequest(User user, Provider provider) {
		return user.getUserIdentities().stream().filter(userIdentity -> userIdentity.getProvider().equals(provider))
				.findFirst()
				.map(userIdentity -> TokenRequest.builder().userUuid(user.getUuid())
						.subject(userIdentity.getProviderId()).authorities(user.getCommaSeparatedRoles()).build())
				.orElseThrow(() -> new CustomException(env.getProperty(ExceptionMessage.USER_IDENTITY_NOT_FOUND),
						HttpStatus.NOT_FOUND));

	}

    // private AuthTokenModel handleNewSocialUser(String providerId, Provider provider, ProviderModel providerModel) {
    //     // Check if a user exists by email
    //     User user = userDao.userByEmailAndIsEmailVerified(providerModel.getEmail(), Boolean.TRUE);
        
    //     if (user == null) {
    //         // If user doesn't exist, create a new user
    //         user = buildUserBySocials(providerModel.getEmail(), providerModel.getName());
    //         user = userDao.saveUser(user);
    //     }
    
    //     // Create a new UserIdentity for the user
    //     UserIdentity userIdentity = buildUserIdentity(user, null, provider, providerId,
    //     providerModel.getEmail(), providerModel.getPicture());
    //     userIdentityDao.saveUserIdentity(userIdentity);
    
    //     // Generate and return the AuthTokenModel
    //     return jwtUtil.generateToken(
    //             userIdentity.getProviderId(),
    //             user.getCommaSeparatedRoles(),
    //             user.getUuid()
    //     );
    // }
    

    private UserIdentity buildUserIdentity(User user, String phoneNumber, Provider provider, String providerId,
			String email, String picture) {
		UserIdentity userIdentity = new UserIdentity();
		userIdentity.setPhone(phoneNumber);
		userIdentity.setProvider(provider);
		userIdentity.setUuid(UUID.randomUUID().toString());
		userIdentity.setProviderId(providerId);
		userIdentity.setEmail(email);
		userIdentity.setPicture(picture);
		userIdentity.setUser(user);

		return userIdentity;
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
