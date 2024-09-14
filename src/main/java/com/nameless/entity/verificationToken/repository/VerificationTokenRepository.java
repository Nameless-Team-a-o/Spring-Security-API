package com.nameless.entity.verificationToken.repository;

import com.nameless.entity.verificationToken.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    Optional <VerificationToken> findByUserEmail(String userEmail);

    @Override
    void delete(VerificationToken entity);
}