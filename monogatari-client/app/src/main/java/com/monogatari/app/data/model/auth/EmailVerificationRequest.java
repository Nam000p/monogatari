package com.monogatari.app.data.model.auth;

import lombok.Data;

@Data
public class EmailVerificationRequest {
    private String newEmail;
    private String otp;
}