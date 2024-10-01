package com.nameless.entity.user.repository;

import java.util.Optional;

import com.nameless.entity.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

}
