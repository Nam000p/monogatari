package com.monogatari.app.controller;

import com.monogatari.app.dto.auth.*;
import com.monogatari.app.service.AuthService;
import com.monogatari.app.util.CookieUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	private final CookieUtils cookieUtils;

	@PostMapping("/register")
	public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
		String response = authService.register(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		AuthResponse response = authService.login(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
		if (refreshToken == null || refreshToken.isEmpty()) {
			throw new RuntimeException("Refresh Token is missing from cookies!");
		}
		AuthResponse response = authService.refreshToken(refreshToken);
		ResponseCookie jwtRefreshCookie = cookieUtils.generateRefreshJwtCookie(response.getRefreshToken());
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString());
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}

	@PostMapping({"/logout", "/logout/"})
	public ResponseEntity<String> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
		authService.logout(refreshToken);
		ResponseCookie cleanCookie = cookieUtils.getCleanJwtRefreshCookie();
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, cleanCookie.toString());
		return new ResponseEntity<>("Logged out successfully!", headers, HttpStatus.OK);
	}

	@PostMapping("/verify-account")
	public ResponseEntity<String> verifyAccount(@Valid @RequestBody VerifyOtpRequest request) {
		authService.verifyAccount(request);
		return new ResponseEntity<>("Account verified successfully!", HttpStatus.OK);
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
		authService.forgotPassword(request);
		return new ResponseEntity<>("OTP for password reset has been sent to your email.", HttpStatus.OK);
	}

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
		authService.resetPassword(request);
		return new ResponseEntity<>("Password has been reset successfully!", HttpStatus.OK);
	}
}