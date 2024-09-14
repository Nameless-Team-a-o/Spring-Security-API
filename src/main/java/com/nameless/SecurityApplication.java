package com.nameless;

import com.nameless.auth.AuthenticationService;
import com.nameless.dto.RegisterRequestDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static com.nameless.entity.user.model.Role.ADMIN;
import static com.nameless.entity.user.model.Role.MANAGER;

@SpringBootApplication
public class SecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

}
