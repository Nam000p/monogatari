package com.monogatari.app.data.model.user;

import com.monogatari.app.data.model.enums.SystemRole;
import com.monogatari.app.data.model.enums.UserStatus;

import lombok.Data;

@Data
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String avatarUrl;
    private SystemRole role;
    private UserStatus status;
    private Boolean isPremium;
}