package com.rmf.apiserverjava.config;

import static com.rmf.apiserverjava.global.constant.ProfileConstant.*;

import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.rmf.apiserverjava.entity.users.Email;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.repository.EmailRepository;
import com.rmf.apiserverjava.service.UserService;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Profile(MAIN)
@Component
@RequiredArgsConstructor
public class InitRootUser {

	private static final String ROOT_USER_ID = "root";
	private static final String ROOT_USER_PASSWORD = "root";

	private final BCryptPasswordEncoder passwordEncoder;
	private final EntityManager entityManager;
	private final UserService userService;
	private final EmailRepository emailRepository;

	@PostConstruct
	public void init() {
		if (entityManager.find(User.class, ROOT_USER_ID) == null) {
			Email email = Email.builder()
				.email("root@email.com")
				.build();
			User root = User.builder()
				.username(ROOT_USER_ID)
				.password(passwordEncoder.encode(ROOT_USER_PASSWORD))
				.email(email)
				.isAdmin(true)
				.build();
			emailRepository.save(email);
			userService.saveUser(root);
		}
	}
}
