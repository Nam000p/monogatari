package com.monogatari.app.data.model.auth;

import lombok.Data;

@Data
public class VerifyOtpRequest {
	private String email;
	private String otpCode;
}