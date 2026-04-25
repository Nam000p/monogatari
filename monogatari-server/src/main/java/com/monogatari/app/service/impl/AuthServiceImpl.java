package com.monogatari.app.service.impl;

import com.monogatari.app.annotation.LogIgnore;
import com.monogatari.app.annotation.RateLimited;
import com.monogatari.app.annotation.TrackExecutionTime;
import com.monogatari.app.dto.auth.*;
import com.monogatari.app.entity.OtpCode;
import com.monogatari.app.entity.RefreshToken;
import com.monogatari.app.entity.User;
import com.monogatari.app.enums.AuthProvider;
import com.monogatari.app.enums.OtpPurpose;
import com.monogatari.app.enums.UserStatus;
import com.monogatari.app.repository.OtpCodeRepository;
import com.monogatari.app.repository.UserRepository;
import com.monogatari.app.security.CustomUserDetails;
import com.monogatari.app.security.JwtTokenProvider;
import com.monogatari.app.service.AuthService;
import com.monogatari.app.service.EmailService;
import com.monogatari.app.service.RefreshTokenService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	private final AuthenticationManager authenticationManager;

	private final UserRepository userRepository;

	private final OtpCodeRepository otpCodeRepository;

	private final EmailService emailService;

	private final PasswordEncoder passwordEncoder;

	private final JwtTokenProvider jwtTokenProvider;

	private final RefreshTokenService refreshTokenService;

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	@Override
	@Transactional
	@TrackExecutionTime
	@RateLimited(maxRequests = 5, timeWindowMs = 60000)
	@LogIgnore
	public String register(RegisterRequest request) {
		Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
		User user;
	    if (existingUser.isPresent()) {
	        user = existingUser.get();
	        if (user.getStatus() != UserStatus.UNVERIFIED) {
	            throw new IllegalStateException("Error: Email already exists!");
	        }
	        user.setUsername(request.getUsername());
	        user.setPassword(passwordEncoder.encode(request.getPassword()));
	    } else {
	        user = User.builder()
	                .username(request.getUsername())
	                .password(passwordEncoder.encode(request.getPassword()))
	                .email(request.getEmail())
	                .status(UserStatus.UNVERIFIED)
	                .authProvider(AuthProvider.LOCAL)
	                .build();
	    }
		userRepository.save(user);
		otpCodeRepository.deleteByEmailAndPurpose(user.getEmail(), OtpPurpose.VERIFY_ACCOUNT);
		String otp = generateOtp();
		OtpCode otpCode = OtpCode.builder()
				.email(user.getEmail())
				.otpCode(otp)
				.purpose(OtpPurpose.VERIFY_ACCOUNT)
				.expiryDate(Instant.now().plusSeconds(300))
				.build();
		otpCodeRepository.save(otpCode);
		emailService.sendOtpEmail(user.getEmail(), otp, OtpPurpose.VERIFY_ACCOUNT.name());
		return "Account registration successful! Please check your email to verify your account.";
	}

	@Override
	@Transactional
	@TrackExecutionTime
	@RateLimited(maxRequests = 5, timeWindowMs = 60000)
	@LogIgnore
	public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));
        if (user.getStatus() == UserStatus.UNVERIFIED) {
            throw new IllegalStateException("Account is not verified. Please check your email for the verification code.");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        return new AuthResponse(token, refreshToken.getToken());
    }

	@Override
	@Transactional
	@TrackExecutionTime
	@LogIgnore
	public AuthResponse refreshToken(String refreshToken) {
		return refreshTokenService.findByToken(refreshToken)
				.map(refreshTokenService::verifyExpiration)
				.map(RefreshToken::getUser)
				.map(user -> {
					CustomUserDetails userDetails = new CustomUserDetails(user);
		            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		            String newAccessToken = jwtTokenProvider.generateToken(auth);
		            return new AuthResponse(newAccessToken, refreshToken);
				})
				.orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
	}

	@Override
	@Transactional
	public void logout(String refreshToken) {
		if (refreshToken != null && !refreshToken.isEmpty()) {
            refreshTokenService.findByToken(refreshToken)
                .map(RefreshToken::getUser)
                .ifPresent(refreshTokenService::deleteByUser);
        }
	}

	@Override
	@Transactional
	@TrackExecutionTime
	@RateLimited(maxRequests = 5, timeWindowMs = 60000)
	@LogIgnore
	public void verifyAccount(VerifyOtpRequest request) {
		OtpCode otpCode = otpCodeRepository.findByEmailAndOtpCodeAndPurpose(request.getEmail(), request.getOtpCode(), OtpPurpose.VERIFY_ACCOUNT)
				.orElseThrow(() -> new IllegalArgumentException("Invalid OTP!"));
		if (otpCode.isExpired()) {
			otpCodeRepository.delete(otpCode);
			throw new IllegalStateException("OTP has expired!");
		}
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new EntityNotFoundException("User not found!"));
		if (user.getStatus() != UserStatus.UNVERIFIED) {
			throw new IllegalStateException("Account is already verified or locked!");
		}
		user.setStatus(UserStatus.ACTIVE);
		userRepository.save(user);
		otpCodeRepository.deleteByEmailAndPurpose(request.getEmail(), OtpPurpose.VERIFY_ACCOUNT);
	}

	@Override
	@Transactional
	@TrackExecutionTime
	@RateLimited(maxRequests = 3, timeWindowMs = 60000)
	@LogIgnore
	public void forgotPassword(ForgotPasswordRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new EntityNotFoundException("User not found!"));
		if (user.getAuthProvider() != AuthProvider.LOCAL) {
			throw new IllegalStateException("Account is registered using " + user.getAuthProvider().name() + ". Password reset is not allowed.");
		}
		otpCodeRepository.deleteByEmailAndPurpose(user.getEmail(), OtpPurpose.RESET_PASSWORD);
		String otp = generateOtp();
		OtpCode otpCode = OtpCode.builder()
				.email(user.getEmail())
				.otpCode(otp)
				.purpose(OtpPurpose.RESET_PASSWORD)
				.expiryDate(Instant.now().plusSeconds(300))
				.build();
		otpCodeRepository.save(otpCode);
		emailService.sendOtpEmail(user.getEmail(), otp, OtpPurpose.RESET_PASSWORD.name());
	}

	@Override
	@Transactional
	@TrackExecutionTime
	@RateLimited(maxRequests = 3, timeWindowMs = 60000)
	@LogIgnore
	public void resetPassword(ResetPasswordRequest request) {
		OtpCode otpCode = otpCodeRepository.findByEmailAndOtpCodeAndPurpose(request.getEmail(), request.getOtpCode(), OtpPurpose.RESET_PASSWORD)
				.orElseThrow(() -> new IllegalArgumentException("Invalid OTP!"));
		if (otpCode.isExpired()) {
			otpCodeRepository.delete(otpCode);
			throw new IllegalStateException("Otp has expired!");
		}
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new EntityNotFoundException("User not found!"));
		if (user.getAuthProvider() != AuthProvider.LOCAL) {
			throw new IllegalStateException("Account is registered using " + user.getAuthProvider().name() + ". Password reset is not allowed.");
		}
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userRepository.save(user);
		otpCodeRepository.deleteByEmailAndPurpose(request.getEmail(), OtpPurpose.RESET_PASSWORD);
	}

	@LogIgnore
	private String generateOtp() {
		int otp = 100000 + SECURE_RANDOM.nextInt(900000);
		return String.valueOf(otp);
	}
}