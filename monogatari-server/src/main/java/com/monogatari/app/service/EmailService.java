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

    private static final String MANGA_RED = "#E53935";

    private static final String BG_PAGE = "#F5F5F5";

    private static final String TEXT_MAIN = "#212121";

    private static final String TEXT_HINT = "#757575";

    private static final String BUTTON_BLACK = "#000000";

    @Async
    public void sendOtpEmail(String to, String otpCode, String purpose) {
       try {
          MimeMessage mimeMessage = mailSender.createMimeMessage();
          MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

          String subject;
          String title;
          String description;
          String actionLabel;

          switch (purpose) {
          case "VERIFY_ACCOUNT" -> {
                subject = "[Monogatari] Verify Your Account";
                title = "Create Your Account";
                description = "Thank you for joining Monogatari App. Use the verification code below to complete your registration:";
                actionLabel = "VERIFICATION CODE";
            }
          case "RESET_PASSWORD" -> {
                subject = "[Monogatari] Reset Your Password";
                title = "Security Check";
                description = "We received a request to reset your password. Enter the following code in the app to proceed:";
                actionLabel = "RESET CODE";
            }
            default -> throw new IllegalArgumentException("Invalid OTP purpose: " + purpose);
        }

          String htmlContent = buildHtmlTemplate(title, description, actionLabel, otpCode);

          helper.setTo(to);
          helper.setSubject(subject);
          helper.setText(htmlContent, true);

          mailSender.send(mimeMessage);
       } catch (MessagingException e) {
          throw new RuntimeException("Failed to send HTML email: " + e.getMessage());
       }
    }

    private String buildHtmlTemplate(String title, String description, String actionLabel, String otpCode) {
       return "<div style=\"background-color: " + BG_PAGE + "; padding: 50px 20px; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; color: " + TEXT_MAIN + ";\">"
                + "  <div style=\"max-width: 450px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 10px 30px rgba(0,0,0,0.1);\">"
                + "    "
                + "    <div style=\"padding: 40px 30px 20px 30px; text-align: left;\">"
                + "      <h1 style=\"margin: 0; font-size: 24px; font-weight: 900; color: " + MANGA_RED + "; text-transform: uppercase; letter-spacing: -1px;\">Monogatari App</h1>"
                + "    </div>"
                + "    "
                + "    "
                + "    <div style=\"padding: 0 30px 40px 30px; text-align: left;\">"
                + "      <h2 style=\"margin: 0 0 15px 0; color: " + TEXT_MAIN + "; font-size: 28px; font-weight: bold;\">" + title + "</h2>"
                + "      <p style=\"font-size: 16px; line-height: 1.6; color: " + TEXT_HINT + "; margin-bottom: 30px;\">" + description + "</p>"
                + "      "
                + "      "
                + "      <div style=\"background-color: " + BUTTON_BLACK + "; padding: 25px; border-radius: 16px; text-align: center;\">"
                + "        <p style=\"margin: 0 0 10px 0; font-size: 12px; font-weight: bold; color: #ffffff; opacity: 0.7; letter-spacing: 2px;\">" + actionLabel + "</p>"
                + "        <span style=\"font-size: 36px; font-weight: 800; color: #ffffff; letter-spacing: 8px; font-family: monospace;\">" + otpCode + "</span>"
                + "      </div>"
                + "      "
                + "      <p style=\"font-size: 13px; color: " + TEXT_HINT + "; margin-top: 30px; line-height: 1.5;\">"
                + "        This code is valid for <b>5 minutes</b>. <br/>"
                + "        If you didn't request this, you can safely ignore this email."
                + "      </p>"
                + "    </div>"
                + "    "
                + "    "
                + "    <div style=\"padding: 20px 30px; background-color: #fcfcfc; border-top: 1px solid #eeeeee; text-align: center;\">"
                + "      <p style=\"margin: 0; color: " + TEXT_HINT + "; font-size: 11px; font-weight: bold; letter-spacing: 1px; text-transform: uppercase;\">"
                + "        &copy; 2026 MONOGATARI APP ARCHIVE"
                + "      </p>"
                + "    </div>"
                + "  </div>"
                + "</div>";
    }
}