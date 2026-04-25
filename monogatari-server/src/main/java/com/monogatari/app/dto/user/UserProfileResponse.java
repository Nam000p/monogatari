package com.monogatari.app.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.monogatari.app.enums.SystemRole;
import com.monogatari.app.enums.UserStatus;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class UserProfileResponse {
	private Long id;
	private String username;
	private String email;
	private LocalDate birthDate;
	private String avatarUrl;
	private SystemRole role;
	private UserStatus status;
	private String stripeCustomerId;
	private Instant premiumExpiredDate;

	@JsonProperty("isPremium")
	private boolean isPremium;

	private Instant createdAt;
}