package com.monogatari.app.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmailVerificationRequest {
	@NotBlank(message = "Email is required!")
	@Email(message = "Email format is invalid! (e.g user@example.com)")
	@Size(max = 100, message = "Email cannot exceed 100 characters!")
    private String newEmail;
    
	@NotBlank(message = "OTP code is required!")
	@Size(min = 6, max = 6, message = "OTP code must be exactly 6 characters!")
    private String otp;
}