package com.monogatari.app.service;

import com.monogatari.app.dto.auth.*;

public interface AuthService {
	String register(RegisterRequest request);
	
	AuthResponse login(LoginRequest request);

	AuthResponse refreshToken(String refreshToken);
	
	void logout(String refreshToken);
	
	void verifyAccount(VerifyOtpRequest request);
	
	void forgotPassword(ForgotPasswordRequest request);
	
	void resetPassword(ResetPasswordRequest request);
}
