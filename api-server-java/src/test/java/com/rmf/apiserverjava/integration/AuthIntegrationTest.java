package com.rmf.apiserverjava.integration;

import static java.lang.Thread.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmf.apiserverjava.config.annotation.IntegrationTestWithSecurity;
import com.rmf.apiserverjava.entity.jwt.RefreshToken;
import com.rmf.apiserverjava.entity.users.Email;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.utils.JwtUtil;
import com.rmf.apiserverjava.repository.RefreshTokenRepository;
import com.rmf.apiserverjava.repository.UserRepository;
import com.rmf.apiserverjava.security.SecurityRole;
import com.rmf.apiserverjava.security.UserSession;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;

@IntegrationTestWithSecurity
@AutoConfigureMockMvc
@Transactional
public class AuthIntegrationTest {
	@Autowired
	EntityManager em;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	JwtUtil jwtUtil;

	@Autowired
	RefreshTokenRepository refreshTokenRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	private UserSession userSession;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Nested
	@DisplayName("GET /is_login")
	class IsLogin {
		@Test
		@DisplayName("로그인 상태임을 확인한다.")
		void success() throws Exception {
			//Arrange
			String username = UUID.randomUUID().toString();
			String password = "password*&1588";
			Email email = Email.builder().email("email").isVerified(true).build();
			User user = new User(username, passwordEncoder.encode(password), false, email);
			userSession.renewSession(username);
			em.persist(email);
			em.persist(user);
			Cookie access = jwtUtil.createJwtCookie(username, SecurityRole.ROLE_USER.name(), JwtUtil.TokenType.ACCESS);

			//Act
			ResultActions perform = mockMvc.perform(get("/is_login").cookie(access));

			//Assert
			perform.andExpect(status().isOk());
		}

		@Test
		@DisplayName("로그인이 되어 있지 않다면 페이지 접근이 403으로 거부된다")
		void failed() throws Exception {
			//Arrange
			//Act
			ResultActions perform = mockMvc.perform(get("/is_login"));

			//Assert
			perform.andExpect(status().isForbidden());
		}
	}

	@Nested
	@DisplayName("POST /logout")
	class Logout {
		@Test
		@DisplayName("로그인이 되어 있다면 로그아웃에 성공한다.")
		void success() throws Exception {
			//Arrange
			String username = UUID.randomUUID().toString();
			String password = "password*&1588";
			Email email = Email.builder().email("email").isVerified(true).build();
			User user = new User(username, passwordEncoder.encode(password), false, email);
			em.persist(email);
			em.persist(user);
			Cookie access = jwtUtil.createJwtCookie(username, SecurityRole.ROLE_USER.name(), JwtUtil.TokenType.ACCESS);
			mockMvc.perform(post("/login")
				.param("username", username)
				.param("password", password)
			);

			//Act
			ResultActions perform = mockMvc.perform(post("/logout").cookie(access));

			//Assert
			perform.andExpect(status().isOk());
		}

		@Test
		@DisplayName("로그아웃에 성공하면 jwt 비활성 시간 갱신과 세션이 만료되어 유효한 JWT를 전달하여도 API 접근이 되지 않는다.")
		void failedBySessionNotExistByLogout() throws Exception {
			//Arrange
			String username = UUID.randomUUID().toString();
			String password = "password*&1588";
			Email email = Email.builder().email("email").isVerified(true).build();
			User user = new User(username, passwordEncoder.encode(password), false, email);
			em.persist(email);
			em.persist(user);
			Cookie access = jwtUtil.createJwtCookie(username, SecurityRole.ROLE_USER.name(), JwtUtil.TokenType.ACCESS);
			mockMvc.perform(post("/login")
				.param("username", username)
				.param("password", password)
			);

			//Act
			ResultActions perform = mockMvc.perform(post("/logout").cookie(access));

			//Assert
			perform.andExpect(status().isOk());
			mockMvc.perform(get("/is_login").cookie(access)).andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("유저가 존재할 경우 DB에 저장된 RefreshToken을 삭제한다.")
		void successWithDeleteRefreshToken() throws Exception {
			//Arrange
			String username = UUID.randomUUID().toString();
			String password = "password*&1588";
			Email email = Email.builder().email("email").isVerified(true).build();
			User user = new User(username, passwordEncoder.encode(password), false, email);
			Cookie refreshCookie = jwtUtil.createJwtCookie(username, "ROLE_ADMIN", JwtUtil.TokenType.REFRESH);
			Cookie accessCookie = jwtUtil.createJwtCookie(username, "ROLE_ADMIN", JwtUtil.TokenType.ACCESS);
			String ip = "ip";
			userSession.renewSession(username);

			em.persist(email);
			em.persist(user);
			RefreshToken refreshToken = createRefresh(refreshCookie, user, ip);
			em.persist(refreshToken);
			em.flush();
			em.clear();

			//Act
			ResultActions perform = mockMvc.perform(post("/logout")
				.cookie(refreshCookie).cookie(accessCookie));

			//Assert
			em.flush();
			perform.andExpect(status().isOk());
			Optional<User> existUser = userRepository.findById(username);
			assertThat(existUser).isNotEmpty();
			assertThat(refreshTokenRepository.findByUser(existUser.get())).isEmpty();
		}

		@Test
		@DisplayName("유저가 존재하지 않을 경우 세션과 쿠키만 만료시킨다")
		void successWithExpireSessionAndCookie() throws Exception {
			//Arrange
			String username = UUID.randomUUID().toString();
			Cookie refreshCookie = jwtUtil.createJwtCookie(username, "ROLE_ADMIN", JwtUtil.TokenType.REFRESH);
			Cookie accessCookie = jwtUtil.createJwtCookie(username, "ROLE_ADMIN", JwtUtil.TokenType.ACCESS);
			userSession.renewSession(username);

			//Act
			ResultActions perform = mockMvc.perform(post("/logout")
				.cookie(refreshCookie).cookie(accessCookie)).andExpect(status().isOk());

			//Assert
			sleep(1000);
			refreshCookie = jwtUtil.createJwtCookie(username, "ROLE_ADMIN", JwtUtil.TokenType.REFRESH);
			accessCookie = jwtUtil.createJwtCookie(username, "ROLE_ADMIN", JwtUtil.TokenType.ACCESS);
			mockMvc.perform(post("/logout")
				.cookie(refreshCookie).cookie(accessCookie)).andExpect(status().isUnauthorized());
		}
	}

	public RefreshToken createRefresh(Cookie cookie, User user, String ip) {
		return RefreshToken.builder()
			.refreshToken(cookie.getValue())
			.user(user)
			.ip(ip)
			.build();
	}
}
