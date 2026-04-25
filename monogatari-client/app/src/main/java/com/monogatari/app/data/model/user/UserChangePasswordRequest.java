package com.monogatari.app.data.model.user;

import lombok.Data;

@Data
public class UserChangePasswordRequest {
	private String oldPassword;
	private String newPassword;
}