package com.monogatari.app.scheduler;

import com.monogatari.app.enums.UserStatus;
import com.monogatari.app.repository.OtpCodeRepository;
import com.monogatari.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataCleanup {
	private final UserRepository userRepository;
	
	private final OtpCodeRepository otpCodeRepository;

	@Scheduled(fixedRate = 3600000)
	@Transactional
	public void cleanupExpiredData() {
		log.info("Starting periodic data cleanup task...");
		
		Instant userThreshold = Instant.now().minus(24, ChronoUnit.HOURS);
		userRepository.deleteAllByStatusAndCreatedAtBefore(UserStatus.UNVERIFIED, userThreshold);
		
		otpCodeRepository.deleteAllByExpiryDateBefore(Instant.now());

		log.info("Cleanup task completed.");
	}
}