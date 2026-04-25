package com.monogatari.app.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserChangePasswordRequest {
	@NotBlank(message = "Old password is required!")
	private String oldPassword;
	
	@NotBlank(message = "Password is required!")
	@Size(min = 8, max = 255, message = "New password must be between 8 and 255 characters!")
	@Pattern(
			regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()_~?-]).*$", 
			message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character!"
			)
	private String newPassword;
}