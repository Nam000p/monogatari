package com.monogatari.app.config;

import com.monogatari.app.entity.User;
import com.monogatari.app.enums.SystemRole;
import com.monogatari.app.enums.UserStatus;
import com.monogatari.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	@Value("${admin.default.password}")
	private String adminPassword;
	
	@Override
	public void run(String... args) throws Exception {
		if (!userRepository.existsByEmail("admin@monogatari.com")) {
			log.info("==> Seeding default admin user...");
			User admin = User.builder()
					.username("admin")
					.email("admin@monogatari.com")
					.password(passwordEncoder.encode(adminPassword))
					.role(SystemRole.ROLE_ADMIN)
					.status(UserStatus.ACTIVE)
					.build();
			userRepository.save(admin);
			log.info("==> Default admin user created successfully!");
        } else {
            log.debug("==> Admin user already exists. Skipping seeding.");
        }	
	}
}