package com.monogatari.app.service.impl;

import com.monogatari.app.annotation.LogIgnore;
import com.monogatari.app.annotation.RateLimited;
import com.monogatari.app.annotation.TrackExecutionTime;
import com.monogatari.app.dto.auth.EmailChangeRequest;
import com.monogatari.app.dto.auth.EmailVerificationRequest;
import com.monogatari.app.dto.user.UserChangePasswordRequest;
import com.monogatari.app.dto.user.UserProfileResponse;
import com.monogatari.app.dto.user.UserUpdateProfileRequest;
import com.monogatari.app.dto.user.UserUpdateRoleRequest;
import com.monogatari.app.entity.Subscription;
import com.monogatari.app.entity.User;
import com.monogatari.app.enums.AuthProvider;
import com.monogatari.app.enums.UserStatus;
import com.monogatari.app.repository.SubscriptionRepository;
import com.monogatari.app.repository.UserRepository;
import com.monogatari.app.service.EmailService;
import com.monogatari.app.service.FileStorageService;
import com.monogatari.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;

	private final SubscriptionRepository subscriptionRepository;

	private final FileStorageService fileStorageService;

	private final PasswordEncoder passwordEncoder;

	private final EmailService emailService;

    private final Map<String, String> emailOtpCache = new ConcurrentHashMap<>();

	@Value("${app.upload.dir:uploads}")
	private String uploadDirName;

	@Override
	@Transactional(readOnly = true)
	@TrackExecutionTime
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	@TrackExecutionTime
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	@Transactional(readOnly = true)
	@TrackExecutionTime
	public User getCurrentAuthenticateUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentEmail = authentication.getName();
		User currentUser = userRepository.findByEmail(currentEmail)
				.orElseThrow(() -> new EntityNotFoundException("Current user not found!"));
		if (currentUser.getStatus() == UserStatus.LOCKED) {
			throw new IllegalStateException("Account is locked! Access denied.");
		}
		return currentUser;
	}

	@Override
	@Transactional(readOnly = true)
	@TrackExecutionTime
	public UserProfileResponse getMyProfile() {
		User user = getCurrentAuthenticateUser();
		return mapToResponse(user);
	}

	@Override
	@Transactional
	@RateLimited(maxRequests = 10, timeWindowMs = 60000)
	public UserProfileResponse updateMyProfile(UserUpdateProfileRequest request) {
		User user = getCurrentAuthenticateUser();
		if (request.getUsername() != null ) {
			user.setUsername(request.getUsername());
		}
		if (request.getBirthDate() != null) {
			user.setBirthDate(request.getBirthDate());
		}
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
	}

	@Override
    @Transactional
    @RateLimited(maxRequests = 5, timeWindowMs = 60000)
    public void requestEmailChange(EmailChangeRequest request) {
        User user = getCurrentAuthenticateUser();
        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            throw new IllegalStateException("Social accounts cannot change email!");
        }
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect password!");
        }
        if (userRepository.existsByEmail(request.getNewEmail())) {
            throw new IllegalStateException("Email already in use!");
        }
        String otp = String.format("%06d", new Random().nextInt(999999));
        emailOtpCache.put(request.getNewEmail(), otp);
        emailService.sendOtpEmail(request.getNewEmail(), otp, "CHANGE_EMAIL");
    }

	@Override
    @Transactional
    @RateLimited(maxRequests = 5, timeWindowMs = 60000)
    public UserProfileResponse confirmEmailChange(EmailVerificationRequest request) {
        User user = getCurrentAuthenticateUser();
        String savedOtp = emailOtpCache.get(request.getNewEmail());
        if (savedOtp == null || !savedOtp.equals(request.getOtp())) {
            throw new IllegalArgumentException("Invalid or expired OTP!");
        }
        user.setEmail(request.getNewEmail());
        userRepository.save(user);
        emailOtpCache.remove(request.getNewEmail());
        updateSecurityContext(user.getEmail());
        return mapToResponse(user);
    }

	@Override
	@Transactional
	@RateLimited(maxRequests = 5, timeWindowMs = 60000)
	@LogIgnore
	public void changePassword(UserChangePasswordRequest request) {
		User currentUser = getCurrentAuthenticateUser();
		if (currentUser.getAuthProvider() != AuthProvider.LOCAL) {
			throw new IllegalStateException("Account is registered using " + currentUser.getAuthProvider().name() + ". Password change is not allowed.");
		}
		if (request.getOldPassword().equals(request.getNewPassword())) {
			throw new IllegalArgumentException("New password cannot be the same as old password!");
		}
		if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
			throw new IllegalArgumentException("Old password is incorrect!");
		}
		currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
	}

	@Override
	@Transactional
	@RateLimited(maxRequests = 3, timeWindowMs = 60000)
	public void deleteCurrentUser() {
		User currentUser = getCurrentAuthenticateUser();
		currentUser.setStatus(UserStatus.DELETED);
		currentUser.setUsername("Deleted_User_" + currentUser.getId());
		currentUser.setEmail("deleted_" + UUID.randomUUID() + "@trello.mini");
		currentUser.setAvatarUrl(null);
		currentUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
		userRepository.save(currentUser);
		SecurityContextHolder.clearContext();
	}

	@Override
	@Transactional
	@RateLimited(maxRequests = 5, timeWindowMs = 60000)
	public UserProfileResponse updateAvatar(MultipartFile file) {
	    User currentUser = getCurrentAuthenticateUser();
	    String oldAvatarUrl = currentUser.getAvatarUrl();
	    String fileUrl = fileStorageService.storeFile(file, "avatars");
	    currentUser.setAvatarUrl(fileUrl);
	    userRepository.save(currentUser);
	    fileStorageService.deleteFile(oldAvatarUrl);
	    return mapToResponse(currentUser);
	}

	@Override
	@Transactional(readOnly = true)
	@TrackExecutionTime
	public Page<UserProfileResponse> getAllUsers(Pageable pageable) {
		Page<User> users = userRepository.findAllIncludeDeleted(pageable);
		return users.map(this::mapToResponse);
	}

	@Override
	@Transactional
	@RateLimited(maxRequests = 20, timeWindowMs = 60000)
	public void updateUserRole(Long userId, UserUpdateRoleRequest request) {
		User user = userRepository.findByIdIncludeDeleted(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found!"));
		if (user.getRole().equals(request.getNewRole())) {
		    throw new IllegalStateException("User already has the " + request.getNewRole().name() + " role!");
		}
		user.setRole(request.getNewRole());
		userRepository.save(user);
	}

	@Override
	@Transactional
	@RateLimited(maxRequests = 20, timeWindowMs = 60000)
	public void lockUser(Long userId) {
		User user = userRepository.findByIdIncludeDeleted(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found!"));
		user.setStatus(UserStatus.LOCKED);
		userRepository.save(user);
	}

	@Override
	@Transactional
	@RateLimited(maxRequests = 20, timeWindowMs = 60000)
	public void unlockUser(Long userId) {
		User user = userRepository.findByIdIncludeDeleted(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found!"));
		user.setStatus(UserStatus.ACTIVE);
		userRepository.save(user);
	}

	private UserProfileResponse mapToResponse(User user) {
		UserProfileResponse response = new UserProfileResponse();
		response.setId(user.getId());
		response.setUsername(user.getUsername());
		response.setEmail(user.getEmail());
		response.setBirthDate(user.getBirthDate().toString());
		response.setAvatarUrl(user.getAvatarUrl());
		response.setRole(user.getRole());
		response.setStatus(user.getStatus());
		response.setStripeCustomerId(user.getStripeCustomerId());
		response.setCreatedAt(user.getCreatedAt());
		Optional<Subscription> subscriptionOpt = subscriptionRepository.findByUserId(user.getId());
		if (subscriptionOpt.isPresent()) {
			Subscription sub = subscriptionOpt.get();
			Instant expiredDate = sub.getCurrentPeriodEnd();
			response.setPremiumExpiredDate(expiredDate);
			boolean isActive = sub.getStatus() != null &&
							   "ACTIVE".equalsIgnoreCase(sub.getStatus().name()) &&
							   expiredDate != null &&
							   expiredDate.isAfter(Instant.now());
			response.setPremium(isActive);
		} else {
			response.setPremium(false);
			response.setPremiumExpiredDate(null);
		}
		return response;
	}

	private void updateSecurityContext(String newEmail) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
	            newEmail, auth.getCredentials(), auth.getAuthorities());
	    SecurityContextHolder.getContext().setAuthentication(newAuth);
	}
}