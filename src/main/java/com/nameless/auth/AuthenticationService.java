package com.nameless.auth;

import com.nameless.entity.token.TokenUtils;
import com.nameless.security.jwt.JwtService;
import com.nameless.dto.AuthRequestDTO;
import com.nameless.dto.AuthResponseDTO;
import com.nameless.dto.RegisterRequestDTO;
import com.nameless.entity.token.Token;
import com.nameless.entity.token.TokenRepository;
import com.nameless.entity.user.model.User;
import com.nameless.entity.user.Repository.UserRepository;
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

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  @Value("${application.security.jwt.refresh-token.expiration}")
  private long refreshTokenExpirationDays;

  public AuthResponseDTO register(RegisterRequestDTO request) {
    var user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .build();
    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);

    saveUserToken(savedUser, refreshToken);

    return AuthResponseDTO.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
  }

  public AuthResponseDTO authenticate(AuthRequestDTO request) {
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            )
    );

    var user = repository.findByEmail(request.getEmail()).orElseThrow();
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
    var validTokens = tokenRepository.findAllValidTokenByUser(user.getId());

    boolean isTokenValid = validTokens.stream()
            .anyMatch(t -> !t.isRevoked());

    if (!isTokenValid) {
      return Optional.empty();
    }
    return repository.findByEmail(email);
  }

  private void saveUserToken(User user, String refreshToken) {
    var token = Token.builder()
            .user(user)
            .token(TokenUtils.hashToken(refreshToken))
            .expiration(LocalDateTime.now().plusDays(refreshTokenExpirationDays))
            .revoked(false)
            .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (!validUserTokens.isEmpty()) {
        for (Token token : validUserTokens) {
            token.setRevoked(true);
        }
        tokenRepository.saveAll(validUserTokens);
    }
  }
}