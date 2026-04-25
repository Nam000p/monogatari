package com.monogatari.app.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
	@NotBlank(message = "Username is required!")
	@Size(min = 3, max = 255, message = "Username must be between 3 and 255 characters!")
	@Pattern(
			regexp = "^[a-zA-Z0-9._\\s-]+$",
			message = "Username can only contains letters, numbers, dots, hyphens, and underscores!"
			)
	private String username;

	@NotBlank(message = "Password is required!")
	@Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters!")
	@Pattern(
			regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()_~?-]).*$", 
			message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character!"
			)
	private String password;

	@NotBlank(message = "Email is required!")
	@Email(message = "Email format is invalid! (e.g user@example.com)")
	@Size(max = 100, message = "Email cannot exceed 100 characters!")
	private String email;
}