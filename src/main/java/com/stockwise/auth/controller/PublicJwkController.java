package com.stockwise.auth.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockwise.auth.constant.ApiUrl;
import com.stockwise.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PublicJwkController {

	private final AuthService authService;

	/**
	 * Public jwk json
	 * 
	 * @author Mindbowser | rohit.kavthekar@mindbowser.com
	 * @return {@link ResponseEntity}
	 */
	@GetMapping(value = ApiUrl.PUBLIC_JWK_JSON, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> publicJwkJson() {

		String jsonString = authService.publicJwkJson();

		return ResponseEntity.ok(jsonString);
	}
}
