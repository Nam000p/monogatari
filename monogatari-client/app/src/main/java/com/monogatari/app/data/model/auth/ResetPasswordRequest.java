package com.monogatari.app.data.model.auth;

import lombok.Data;

@Data
public class ResetPasswordRequest {
	private String email;
	private String otpCode;
	private String newPassword;
}