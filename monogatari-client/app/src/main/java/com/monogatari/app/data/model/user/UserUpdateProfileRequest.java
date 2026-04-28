package com.monogatari.app.data.model.user;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserUpdateProfileRequest {
	private String username;
	private LocalDate birthDate;
}