package com.nameless.entity.verificationToken.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import com.nameless.entity.verificationToken.model.VerificationToken;
import com.nameless.entity.verificationToken.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;
    private final JavaMailSender mailSender;

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

    public void newVerifyToken(String userEmail) {
        Optional<VerificationToken> oldToken = tokenRepository.findByUserEmail(userEmail);
        if (oldToken.isPresent()) {
            VerificationToken verificationToken = oldToken.get();
            tokenRepository.delete(verificationToken);
        }

        String newToken = UUID.randomUUID().toString();
        LocalDateTime newExpirationDate = LocalDateTime.now().plusHours(1);
        saveToken(newToken, userEmail, newExpirationDate);

        String verificationLink = "http://localhost:8080/api/v1/auth/verify/" + newToken;
        sendVerificationEmail(userEmail, verificationLink);
    }
    public void sendVerificationEmail(String userEmail, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("Verify Your Account");
        message.setText("Please verify your account by clicking the following link:\n" + verificationLink);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            // Log the exception with a detailed message
            System.err.println("Failed to send verification email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
