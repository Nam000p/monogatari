package com.monogatari.app.service;

public interface OtpCodeService {
	String generateOtp(String email, String purpose);
	
    boolean validateOtp(String email, String code, String purpose);
    
    void clearExpiredOtp();
}