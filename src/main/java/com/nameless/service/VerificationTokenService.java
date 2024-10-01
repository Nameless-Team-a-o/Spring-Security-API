package com.nameless.service;

import com.nameless.entity.user.repository.UserRepository;
import com.nameless.entity.user.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import com.nameless.entity.verificationToken.model.VerificationToken;
import com.nameless.entity.verificationToken.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    public void saveToken(String token, String userEmail, LocalDateTime expirationDate) {
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .userEmail(userEmail)
                .expirationDate(expirationDate)
                .build();
        tokenRepository.save(verificationToken);
    }

    public boolean verifyToken(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken != null && !verificationToken.isUsed() && LocalDateTime.now().isBefore(verificationToken.getExpirationDate())) {
            verificationToken.setUsed(true);
            tokenRepository.save(verificationToken);
            return true;
        }
        return false;
    }

    public void newVerifyToken(String userEmail) throws Exception {
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isEmpty()) {
            throw new Exception("No account found for the provided email.");
        }



        Optional<VerificationToken> oldTokenOpt = tokenRepository.findByUserEmail(userEmail);

        VerificationToken oldtoken = oldTokenOpt.orElse(null);
        if(oldtoken != null && oldtoken.isUsed() ) {
                throw new Exception("Account is already verified.");
        }

        if (oldTokenOpt.isPresent()) {
            VerificationToken verificationToken = oldTokenOpt.get();
            tokenRepository.delete(verificationToken);
        }

        String newToken = UUID.randomUUID().toString();
        LocalDateTime newExpirationDate = LocalDateTime.now().plusHours(1);
        saveToken(newToken, userEmail, newExpirationDate);

        String verificationLink = "http://localhost:8080/api/v1/auth/verify/" + newToken;
        sendVerificationEmail(userEmail, verificationLink);
    }
    public void sendVerificationEmail(String userEmail, String verificationLink) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "utf-8");

            String htmlMessage = "<div style='font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: auto; background-color: #f4f4f4; padding: 20px; border-radius: 10px;'>"
                    + "<h2 style='color: #4a90e2; font-size: 24px; text-align: center;'>Verify Your Account</h2>"
                    + "<p style='font-size: 16px; line-height: 1.6; color: #555;'>Hello,</p>"
                    + "<p style='font-size: 16px; line-height: 1.6; color: #555;'>Thank you for registering! To complete your registration, please click the button below to verify your account:</p>"
                    + "<div style='text-align: center; margin: 20px 0;'>"
                    + "<a href='" + verificationLink + "' style='display: inline-block; padding: 15px 25px; font-size: 16px; color: #ffffff; background-color: #007bff; text-decoration: none; border-radius: 8px;'>"
                    + "Verify Your Account"
                    + "</a>"
                    + "</div>"
                    + "<p style='font-size: 14px; color: #999;'>If you do not see the button above or if the link is not clickable, please check your spam or junk folder. Ensure that our email address is not marked as spam. Adding our email address to your contacts can help you receive future messages without issues.</p>"
                    + "<p style='font-size: 16px; line-height: 1.6; color: #555;'>Best regards,<br>Nameless Team</p>"
                    + "</div>";

            helper.setTo(userEmail);
            helper.setSubject("Verify Your Account");
            helper.setText(htmlMessage, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
