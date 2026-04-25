package com.monogatari.app.service;

import com.monogatari.app.dto.auth.EmailChangeRequest;
import com.monogatari.app.dto.auth.EmailVerificationRequest;
import com.monogatari.app.dto.user.UserChangePasswordRequest;
import com.monogatari.app.dto.user.UserProfileResponse;
import com.monogatari.app.dto.user.UserUpdateProfileRequest;
import com.monogatari.app.dto.user.UserUpdateRoleRequest;
import com.monogatari.app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserService {
	Optional<User> findById(Long id);

	Optional<User> findByEmail(String email);

	User getCurrentAuthenticateUser();

	UserProfileResponse getMyProfile();

	UserProfileResponse updateMyProfile(UserUpdateProfileRequest request);

	void requestEmailChange(EmailChangeRequest request);

	UserProfileResponse confirmEmailChange(EmailVerificationRequest request);
	
	UserProfileResponse updateAvatar(MultipartFile file);
	
	void changePassword(UserChangePasswordRequest request);
	
	void deleteCurrentUser();
	
	Page<UserProfileResponse> getAllUsers(Pageable pageable);
	
	void updateUserRole(Long userId, UserUpdateRoleRequest request);
	
	void lockUser(Long userId);
	
	void unlockUser(Long userId);
}