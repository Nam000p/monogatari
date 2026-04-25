package com.monogatari.app.controller;

import com.monogatari.app.dto.user.UserChangePasswordRequest;
import com.monogatari.app.dto.user.UserProfileResponse;
import com.monogatari.app.dto.user.UserUpdateProfileRequest;
import com.monogatari.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	
	@GetMapping("/profile")
	public ResponseEntity<UserProfileResponse> getMyProfile() {
		UserProfileResponse response = userService.getMyProfile();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PutMapping("/profile")
	public ResponseEntity<UserProfileResponse> updateMyProfile(@Valid @RequestBody UserUpdateProfileRequest request) {
		UserProfileResponse response = userService.updateMyProfile(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/avatar")
	public ResponseEntity<UserProfileResponse> updateAvatar(@RequestPart("file") MultipartFile file) {
		UserProfileResponse response = userService.updateAvatar(file);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/change-password")
	public ResponseEntity<String> changePassword(@Valid @RequestBody UserChangePasswordRequest request) {
		userService.changePassword(request);
		return new ResponseEntity<>("Password changed successfully!", HttpStatus.OK);
	}
	
	@DeleteMapping("/me")
	public ResponseEntity<String> deleteMyAccount() {
		userService.deleteCurrentUser();
		return new ResponseEntity<>("Account deleted successfully!", HttpStatus.OK);
	}
}