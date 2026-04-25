package com.monogatari.app.repository;

import com.monogatari.app.entity.OtpCode;
import com.monogatari.app.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
	Optional<OtpCode> findByEmailAndOtpCodeAndPurpose(String email, String otpCode, OtpPurpose purpose);
	
	void deleteByEmailAndPurpose(String email, OtpPurpose purpose);
	
	void deleteAllByExpiryDateBefore(Instant dateTime);
}