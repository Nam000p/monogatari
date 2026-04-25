package com.monogatari.app.dto.user;

import com.monogatari.app.enums.SystemRole;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserUpdateRoleRequest {
	@NotNull(message = "New role is required! (ADMIN or USER)")
	private SystemRole newRole;
}