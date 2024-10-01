package com.nameless.entity.refreshToken.model;

import com.nameless.entity.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RefreshToken {

  @Id
  @GeneratedValue
  public Integer id;

  @Column(unique = true)
  public String token;

  private boolean revoked;

  private LocalDateTime expiration;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;

  public boolean isExpired() {
    return expiration.isBefore(LocalDateTime.now());
  }
}
