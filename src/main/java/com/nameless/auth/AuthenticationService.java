package com.nameless.auth;

import com.nameless.service.TokenHashingService;
import com.nameless.entity.verificationToken.model.VerificationToken;
import com.nameless.entity.verificationToken.repository.VerificationTokenRepository;
import com.nameless.service.VerificationTokenService;
import com.nameless.jwt.JwtService;
import com.nameless.dto.AuthRequestDTO;
import com.nameless.dto.AuthResponseDTO;
import com.nameless.dto.RegisterRequestDTO;
import com.nameless.entity.refreshToken.model.RefreshToken;
import com.nameless.entity.refreshToken.repository.RefreshTokenRepository;
import com.nameless.entity.user.model.User;
import com.nameless.entity.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  @Value("${application.security.jwt.refresh-token.expiration}")
  private long refreshTokenExpirationDays;
  private final VerificationTokenService verificationTokenService;
  private final VerificationTokenRepository verificationTokenRepository;

  public boolean register(RegisterRequestDTO request) {
    Optional<User> userOptional = repository.findByEmail(request.getEmail());
    if(userOptional.isPresent()) {
      return false;
    }
    var user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .build();
    User savedUser = repository.save(user);
    String verificationToken = UUID.randomUUID().toString();
    String verificationLink = "http://localhost:8080/api/v1/auth/verify/" + verificationToken;
    verificationTokenService.saveToken(verificationToken, request.getEmail() , LocalDateTime.now().plusHours(1));
    verificationTokenService.sendVerificationEmail(request.getEmail(), verificationLink);
    return true;
  }

  public AuthResponseDTO authenticate(AuthRequestDTO request) {
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            )
    );

    var user = repository.findByEmail(request.getEmail()).orElseThrow();
    Optional <VerificationToken> verificationToken = verificationTokenRepository.findByUserEmail(request.getEmail());
    boolean verified = verificationToken.get().isUsed() ;
    if (!verified) {
      throw new RuntimeException("Account is not verified");
    }

    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, refreshToken); // Save the hashed refresh token

    return AuthResponseDTO.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
  }

  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsernameFromRefresh(refreshToken);

    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail).orElseThrow();

      if (jwtService.isRefreshTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);

        var authResponse = AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      } else {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      }
    } else {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
  }


  public Optional<User> getUserInfo(String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return Optional.empty();
    }

    String token = authHeader.substring(7);
    if (jwtService.isAccessTokenExpired(token)) {
      return Optional.empty();
    }

    String email = jwtService.extractUsernameFromAccess(token);
    if (email == null) {
      return Optional.empty();
    }

    Optional<User> userOptional = repository.findByEmail(email);
    if (userOptional.isEmpty()) {
      return Optional.empty();
    }

    var user = userOptional.get();
    var validTokens = refreshTokenRepository.findAllValidTokenByUser(user.getId());

    boolean isTokenValid = validTokens.stream()
            .anyMatch(t -> !t.isRevoked());

    if (!isTokenValid) {
      return Optional.empty();
    }
    return repository.findByEmail(email);
  }

  private void saveUserToken(User user, String refreshToken) {
    var token = RefreshToken.builder()
            .user(user)
            .token(TokenHashingService.hashToken(refreshToken))
            .expiration(LocalDateTime.now().plusDays(refreshTokenExpirationDays))
            .revoked(false)
            .build();
    refreshTokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = refreshTokenRepository.findAllValidTokenByUser(user.getId());
    if (!validUserTokens.isEmpty()) {
        for (RefreshToken refreshToken : validUserTokens) {
            refreshToken.setRevoked(true);
        }
        refreshTokenRepository.saveAll(validUserTokens);
    }
  }

  public boolean verify(String verToken) {
    return verificationTokenService.verifyToken(verToken);

  }

  public void newVerifyToken(String userEmail) throws Exception {
    verificationTokenService.newVerifyToken(userEmail);
  }
}