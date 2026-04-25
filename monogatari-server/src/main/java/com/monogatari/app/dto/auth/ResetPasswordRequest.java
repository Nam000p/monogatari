package com.monogatari.app.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
	@NotBlank(message = "Email is required!")
	@Email(message = "Email format is invalid! (e.g user@example.com)")
	@Size(max = 100, message = "Email cannot exceed 100 characters!")
	private String email;
	
	@NotBlank(message = "OTP code is required!")
	@Size(min = 6, max = 6, message = "OTP code must be exactly 6 characters!")
	private String otpCode;
	
	@NotBlank(message = "New password is required!")
	@Size(min = 8, max = 255, message = "New password must be between 8 and 255 characters!")
	@Pattern(
			regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()_~?-]).*$", 
			message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character!"
			)
	private String newPassword;
}