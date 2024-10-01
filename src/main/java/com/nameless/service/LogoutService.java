package com.nameless.service;

import com.nameless.entity.refreshToken.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void logout(
          HttpServletRequest request,
          HttpServletResponse response,
          Authentication authentication
  ) {
    final String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return;
    }

    String rawRefreshToken = authHeader.substring(7);
    String hashedRefreshToken = TokenHashingService.hashToken(rawRefreshToken);

    // Find the token by the hashed refresh token
    var storedToken = refreshTokenRepository.findByTokenHash(hashedRefreshToken).orElse(null);

    if (storedToken != null) {
      // Mark the token as revoked and expired
      storedToken.setRevoked(true);
      storedToken.setExpiration(LocalDateTime.now().minusDays(1));
      refreshTokenRepository.save(storedToken);

      // Clear the SecurityContext
      SecurityContextHolder.clearContext();
    }
  }
}
