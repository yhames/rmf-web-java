package com.rmf.apiserverjava.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmf.apiserverjava.config.annotation.IntegrationTestWithSecurity;
import com.rmf.apiserverjava.entity.users.Email;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.utils.JwtUtil;
import com.rmf.apiserverjava.repository.TaskEventLogRepository;
import com.rmf.apiserverjava.security.SecurityRole;
import com.rmf.apiserverjava.security.UserSession;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;

@IntegrationTestWithSecurity
@AutoConfigureMockMvc
@Transactional
public class SecurityIntegrationTest {
	@Autowired
	EntityManager em;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TaskEventLogRepository taskEventLogRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserSession userSession;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Value("${spring.jwt.accessTokenName}")
	String accessTokenName;

	@Value("${spring.jwt.refreshTokenName}")
	String refreshTokenName;

	Cookie access;
	String username;
	String password;

	@BeforeEach
	void setUp() {
		username = "tokenUser";
		password = "password*&1588";
		Email email = Email.builder().email("email").isVerified(true).build();
		User user = new User(username, passwordEncoder.encode(password), false, email);
		userSession.renewSession(username);
		em.persist(email);
		em.persist(user);
		em.flush();
		access = jwtUtil.createJwtCookie(username, SecurityRole.ROLE_USER.name(), JwtUtil.TokenType.ACCESS);
		em.clear();
	}

	@Nested
	@DisplayName("POST /login")
	class Login {
		@Test
		@DisplayName("올바른 Id, Password를 입력하면 로그인에 성공하고 JWT 쿠키를 반환하여야 한다.")
		void success() throws Exception {
			//Arrange
			//Act
			ResultActions perform = mockMvc.perform(post("/login")
				.param("username", username)
				.param("password", password)
			);

			//Assert
			perform
				.andExpect(status().isOk())
				.andExpect(cookie().exists(accessTokenName))
				.andExpect(cookie().exists(refreshTokenName));
		}

		@Test
		@DisplayName("Admin 유저도 올바른 Id, Password를 입력하면 로그인에 성공하고 JWT 쿠키를 반환하여야 한다.")
		void adminUserLoginSuccess() throws Exception {
			//Arrange
			username = "adminUser";
			password = "password*&1588";
			Email email = Email.builder().email("emailAdmin").isVerified(true).build();
			User user = new User(username, passwordEncoder.encode(password), true, email);
			em.persist(email);
			em.persist(user);
			em.flush();
			em.clear();

			//Act
			ResultActions perform = mockMvc.perform(post("/login")
				.param("username", username)
				.param("password", password)
			);

			//Assert
			perform
				.andExpect(status().isOk())
				.andExpect(cookie().exists(accessTokenName))
				.andExpect(cookie().exists(refreshTokenName));
		}

		@Test
		@DisplayName("틀린 id를 입력하면 로그인에 실패하고 401 예외가 발생하고 JWT Refresh 쿠키를 반환하지 말아야 한다.")
		void failByWrongId() throws Exception {
			//Arrange
			String wrongUsername = username + "wrong";

			//Act
			ResultActions perform = mockMvc.perform(post("/login")
				.param("username", wrongUsername)
				.param("password", password)
			);

			//Assert
			perform
				.andExpect(status().isUnauthorized())
				.andExpect(cookie().doesNotExist(refreshTokenName));
		}

		@Test
		@DisplayName("틀린 비밀번호를 입력하면 로그인에 실패하고 401 예외가 발생하고 JWT Refresh 쿠키를 반환하지 말아야 한다.")
		void failByWrongPassword() throws Exception {
			//Arrange
			String wrongPassword = password + "wrong";

			//Act
			ResultActions perform = mockMvc.perform(post("/login")
				.param("username", username)
				.param("password", wrongPassword)
			);

			//Assert
			perform
				.andExpect(status().isUnauthorized())
				.andExpect(cookie().doesNotExist(refreshTokenName));
		}
	}

	@Nested
	@DisplayName("Session")
	class Session {

		@Test
		@DisplayName("세션이 존재하지 않을 경우 JWT를 전달하여도 요청에 대해 401 예외가 발생해야 한다.")
		void failBySessionNotExist() throws Exception {
			//Arrange
			Email email = Email.builder().email("newEmail@email.com").isVerified(true).build();
			String newName = "newUser";
			User user = new User(newName, passwordEncoder.encode(password), false, email);
			em.persist(email);
			em.persist(user);
			em.flush();
			Cookie token = jwtUtil.createJwtCookie(newName, SecurityRole.ROLE_USER.name(), JwtUtil.TokenType.ACCESS);
			em.clear();

			//Act
			ResultActions perform = mockMvc.perform(get("/is_login").cookie(token));

			//Assert
			perform.andExpect(status().isUnauthorized());
		}
	}

	@Nested
	@DisplayName("Auth")
	class Auth {

		@Test
		@DisplayName("인증받지 않은 유저의 요청은 403 예외가 발생한다.")
		void failByNotAuthorized() throws Exception {
			//Arrange
			//Act
			ResultActions perform = mockMvc.perform(get("/is_login"));

			//Assert
			perform.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("요청 유저가 인가에 필요한 Role이 없을 경우 403 예외가 발생한다.")
		void failAuthenticationByRole() throws Exception {
			//Arrange
			//Act
			ResultActions perform = mockMvc.perform(get("/admin/users").cookie(access));

			//Assert
			perform.andExpect(status().isForbidden());
		}
	}
}
