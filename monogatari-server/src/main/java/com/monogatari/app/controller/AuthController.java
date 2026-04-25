package com.monogatari.app.controller;

import com.monogatari.app.dto.auth.*;
import com.monogatari.app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;
	
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