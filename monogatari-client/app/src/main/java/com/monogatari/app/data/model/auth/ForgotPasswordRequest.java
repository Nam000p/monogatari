package com.monogatari.app.data.model.auth;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
	private String email;
}