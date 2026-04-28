package com.monogatari.app.data.model.user;

import com.google.gson.annotations.SerializedName;
import com.monogatari.app.data.model.enums.SystemRole;
import com.monogatari.app.data.model.enums.UserStatus;

import java.time.Instant;
import java.time.LocalDate;

import lombok.Data;

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

    @SerializedName("isPremium")
    private boolean isPremium;

    private Instant createdAt;
}