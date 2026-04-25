package com.monogatari.app.service.impl;

import com.monogatari.app.annotation.LogIgnore;
import com.monogatari.app.entity.RefreshToken;
import com.monogatari.app.entity.User;
import com.monogatari.app.repository.RefreshTokenRepository;
import com.monogatari.app.repository.UserRepository;
import com.monogatari.app.service.RefreshTokenService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

	@Value("${jwt.refresh-token.expiration}")
    private long refreshTokenDurationMs;

    @Override
    @LogIgnore
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    @LogIgnore
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
            .orElse(new RefreshToken());
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @LogIgnore
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new sign-in request");
        }
        return token;
    }

	@Override
	@Transactional
	public void deleteByUser(User user) {
		refreshTokenRepository.deleteByUser(user);
	}
}