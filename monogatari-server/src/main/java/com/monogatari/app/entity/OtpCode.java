package com.monogatari.app.entity;

import jakarta.persistence.*;
import lombok.*;

import com.monogatari.app.enums.OtpPurpose;

import java.time.Instant;

@Entity
@Table(name = "otp_codes", indexes = {
		@Index(name = "idx_otp_email", columnList = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpCode {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 100)
	private String email;
	
	@Column(name = "otp_code", nullable = false, length = 6)
	private String otpCode;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OtpPurpose purpose;
	
	@Column(name = "expiry_date", nullable = false)
	private Instant expiryDate;
	
	public boolean isExpired() {
		return Instant.now().isAfter(this.expiryDate);
	}
}