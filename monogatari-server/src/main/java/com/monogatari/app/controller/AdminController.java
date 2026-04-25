package com.monogatari.app.controller;

import com.monogatari.app.dto.user.UserProfileResponse;
import com.monogatari.app.dto.user.UserUpdateRoleRequest;
import com.monogatari.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
	private final UserService userService;
	
	@GetMapping("/users")
	public ResponseEntity<Page<UserProfileResponse>> getAllUsers(Pageable pageable) {
		Page<UserProfileResponse> responses = userService.getAllUsers(pageable);
		return new ResponseEntity<>(responses, HttpStatus.OK);
	}
	
	@PatchMapping("/users/{userId}/role")
	public ResponseEntity<String> updateUserRole (@PathVariable Long userId, @Valid @RequestBody UserUpdateRoleRequest request) {
		userService.updateUserRole(userId, request);
		return new ResponseEntity<>("User role updated successfully!", HttpStatus.OK);
	}
	
	@DeleteMapping("/users/{userId}")
	public ResponseEntity<String> lockUser(@PathVariable Long userId) {
		userService.lockUser(userId);
		return new ResponseEntity<>("User deleted successfully!", HttpStatus.OK);
	}
	
	@PatchMapping("/users/{userId}/unlock")
	public ResponseEntity<String> unlockUser(@PathVariable Long userId) {
		userService.unlockUser(userId);
		return new ResponseEntity<>("User restored successfully!", HttpStatus.OK);
	}
}
