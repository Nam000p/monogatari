package com.monogatari.app.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender mailSender;
	
	@Async
//	@LogIgnore
	public void sendOtpEmail(String to, String otpCode, String purpose) {
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

			String subject;
			String title;
			String description;
			String actionText;

			switch (purpose) {
			case "VERIFY_ACCOUNT" -> {
                subject = "[Monogatari] Begin your journey";
                title = "Welcome, Traveler";
                description = "Your library of endless stories is almost ready. Use the parchment code below to verify your account and start reading:";
                actionText = "Verification Code";
            }
			case "RESET_PASSWORD" -> {
                subject = "[Monogatari] Recover your key";
                title = "Reset Your Password";
                description = "It seems you've lost your key to the library. Use the code below to safely restore your access:";
                actionText = "Reset Code";
            }
            default -> throw new IllegalArgumentException("Invalid OTP purpose: " + purpose);
        }

			String htmlContent = buildHtmlTemplate(title, description, actionText, otpCode);

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(htmlContent, true);

			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new RuntimeException("Failed to send HTML email: " + e.getMessage());
		}
	}

	private String buildHtmlTemplate(String title, String description, String actionText, String otpCode) {
		return "<div style=\"background-color: #f4ecd8; padding: 40px 10px; font-family: 'Georgia', serif; color: #2c2c2c;\">"
                + "  <div style=\"max-width: 500px; margin: 0 auto; background-color: #fdfbf7; border: 1px solid #d3c6a3; border-radius: 4px; box-shadow: 0 4px 15px rgba(0,0,0,0.05);\">"
                + "    <div style=\"padding: 30px; text-align: center; border-bottom: 1px double #d3c6a3;\">"
                + "      <h1 style=\"margin: 0; font-size: 28px; font-style: italic; color: #5d4037; letter-spacing: 2px;\">Monogatari</h1>"
                + "    </div>"
                + "    <div style=\"padding: 40px 30px; text-align: center;\">"
                + "      <h2 style=\"margin-top: 0; color: #3e2723; font-size: 20px; font-weight: normal;\">" + title + "</h2>"
                + "      <p style=\"font-size: 16px; line-height: 1.8; color: #4e342e; margin-bottom: 30px;\">" + description + "</p>"
                + "      <div style=\"margin: 30px 0; padding: 20px; background-color: #f9f4e8; border-top: 1px solid #d3c6a3; border-bottom: 1px solid #d3c6a3;\">"
                + "        <p style=\"margin: 0 0 10px 0; font-size: 12px; color: #8d6e63; text-transform: uppercase; letter-spacing: 2px;\">" + actionText + "</p>"
                + "        <span style=\"font-size: 32px; font-weight: bold; color: #2c2c2c; letter-spacing: 10px;\">" + otpCode + "</span>"
                + "      </div>"
                + "      <p style=\"font-size: 13px; color: #a1887f; font-style: italic;\">Valid for 5 minutes. If you didn't request this, just close this chapter.</p>"
                + "    </div>"
                + "    <div style=\"padding: 20px; text-align: center; background-color: #efe5d0; border-top: 1px solid #d3c6a3; border-radius: 0 0 4px 4px;\">"
                + "      <p style=\"margin: 0; color: #8d6e63; font-size: 11px; letter-spacing: 1px;\">&copy; 2026 MONOGATARI ARCHIVE</p>"
                + "    </div>"
                + "  </div>"
                + "</div>";
    }
}