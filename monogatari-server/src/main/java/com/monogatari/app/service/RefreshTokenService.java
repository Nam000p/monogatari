package com.monogatari.app.service;

import com.monogatari.app.entity.RefreshToken;
import com.monogatari.app.entity.User;

import java.util.Optional;

public interface RefreshTokenService {
    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(Long userId);

    RefreshToken verifyExpiration(RefreshToken token);

    void deleteByUser(User user);
}