package com.nameless.auth;


import com.nameless.dto.UserInfoDTO;
import com.nameless.entity.user.model.User;
import com.nameless.dto.AuthRequestDTO;
import com.nameless.dto.AuthResponseDTO;
import com.nameless.dto.RegisterRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;


  @PostMapping("/register")
  public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
    return ResponseEntity.ok(service.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDTO> authenticate(@RequestBody AuthRequestDTO request) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    service.refreshToken(request, response);
  }


  @GetMapping("/user_info")
  public ResponseEntity<UserInfoDTO> getUserInfo(@RequestHeader("Authorization") String authorizationHeader) {
    try {
      Optional<User> optionalUser = service.getUserInfo(authorizationHeader);
      if (optionalUser.isPresent()) {
        User user = optionalUser.get();
        UserInfoDTO userInfo = UserInfoDTO.builder()
                .username(user.getUzerName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
        return ResponseEntity.ok(userInfo);
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
      }
    } catch (Exception e) {

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

}
