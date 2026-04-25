package com.monogatari.app.security;

import com.monogatari.app.entity.User;
import com.monogatari.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final UserService userService;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userService.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Account with email " + email + " not found"));
		return new CustomUserDetails(user);
	}
}