package com.nameless.entity.refreshToken.repository;

import java.util.List;
import java.util.Optional;

import com.nameless.entity.refreshToken.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

  @Query(value = """
      select t from RefreshToken t inner join User u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.revoked = false)\s
      """)
  List<RefreshToken> findAllValidTokenByUser(Long id);

  Optional<RefreshToken> findByToken(String token);

  @Query("SELECT t FROM RefreshToken t WHERE t.token = :tokenHash")
  Optional<RefreshToken> findByTokenHash(String tokenHash);

}
