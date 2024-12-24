package com.stockwise.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockwise.auth.constant.ApiUrl;
import com.stockwise.common.model.AuthTokenModel;
import com.stockwise.user.dto.SocialSigningDto;
import com.stockwise.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiUrl.PUBLIC_BASE_URL)
@RequiredArgsConstructor
public class UserController {
    
    public final UserService userService;

    @PostMapping(ApiUrl.SOCIAL_SIGNIN)
	public ResponseEntity<AuthTokenModel> socialSignin(@Valid @RequestBody SocialSigningDto dto) {
		AuthTokenModel response = userService.socialSigning(dto);

        return ResponseEntity.ok().body(response);
		// return responseBuilder.buildSuccessResponse(env.getProperty(ResponseMessage.SUCCESS), response, HttpStatus.OK);
	}
}
