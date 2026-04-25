package com.monogatari.app.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
	@NotBlank(message = "Email is required!")
	@Email(message = "Email format is invalid! (e.g user@example.com)")
	@Size(max = 100, message = "Email cannot exceed 100 characters!")
	private String email;
}