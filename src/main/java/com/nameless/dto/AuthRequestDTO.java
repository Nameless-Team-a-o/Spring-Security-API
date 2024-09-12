package com.nameless.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDTO {


  @NotBlank(message = "Email cannot be null or blank")
  @Email(message = "Email should be valid")
  private String email;

  @NotBlank(message = "Password cannot be null or blank")
  String password;
}
