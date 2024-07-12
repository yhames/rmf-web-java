package com.rmf.apiserverjava.integration;

import static com.rmf.apiserverjava.service.impl.JwtServiceImpl.*;
import static java.lang.Thread.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmf.apiserverjava.config.annotation.IntegrationTest;
import com.rmf.apiserverjava.entity.jwt.RefreshToken;
import com.rmf.apiserverjava.entity.users.Email;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.exception.custom.BusinessException;
import com.rmf.apiserverjava.global.utils.JwtUtil;
import com.rmf.apiserverjava.repository.RefreshTokenRepository;
import com.rmf.apiserverjava.repository.UserRepository;
import com.rmf.apiserverjava.service.impl.JwtServiceImpl;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class JwtIntegrationTest {
	@Autowired
	EntityManager em;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	JwtServiceImpl jwtService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RefreshTokenRepository refreshTokenRepository;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	JwtUtil jwtUtil;

	@Nested
	@DisplayName("createOrUpdateRefreshToken")
	class CreateOrUpdateRefreshToken {
		@Test
		@DisplayName("DB에 유저가 존재하지 않을 경우 Exception이 발생한다.")
		void userNotExist() throws Exception {
			//Arrange
			HttpServletRequest request = mock(HttpServletRequest.class);

			//Act
			//Assert
			assertThrows(BusinessException.class, () -> {
				jwtService.createOrUpdateRefreshToken("token", "username", request);
			});
		}

		@Test
		@DisplayName("유저가 존재하고 DB에 토큰이 존재하지 않을 경우 새로운 토큰을 저장한다.")
		void tokenNotExist() throws Exception {
			//Arrange
			String username = "username";
			String token = "token";
			String ip = "ip";

			Email email = Email.builder().email("email").build();
			User user = User.builder().username(username).email(email).password("password").isAdmin(true).build();
			em.persist(email);
			em.persist(user);
			em.flush();
			em.clear();
			HttpServletRequest request = mock(HttpServletRequest.class);
			when(request.getRemoteAddr()).thenReturn(ip);

			//Act
			jwtService.createOrUpdateRefreshToken("token", username, request);

			//Assert
			User existUser = userRepository.findById(username).get();
			RefreshToken refreshToken = refreshTokenRepository.findByUser(existUser).get();
			Assertions.assertThat(refreshToken.getRefreshToken()).isEqualTo(token);
			Assertions.assertThat(bCryptPasswordEncoder.matches(ip, refreshToken.getIp())).isTrue();
		}

		@Test
		@DisplayName("유저가 존재하고 DB에 토큰이 존재할 경우 토큰을 업데이트한다.")
		void tokenExist() throws Exception {
			//Arrange
			String username = "username";
			String existToken = "token";
			String newToken = "new token";
			String existIp = "new ip";
			String newIp = "newIp";

			HttpServletRequest request = mock(HttpServletRequest.class);
			when(request.getHeader(X_FORWARDED_FOR_HEADER)).thenReturn(newIp + ", helloIp");

			Email email = Email.builder().email("email").build();
			User user = User.builder().username(username).email(email).password("password").isAdmin(true).build();
			em.persist(email);
			em.persist(user);
			RefreshToken existRefresh = RefreshToken.builder().refreshToken(existToken).user(user).ip(existIp).build();
			em.persist(existRefresh);
			em.flush();

			//Act
			jwtService.createOrUpdateRefreshToken(newToken, username, request);

			//Assert
			User existUser = userRepository.findById(username).get();
			RefreshToken refreshToken = refreshTokenRepository.findByUser(existUser).get();
			Assertions.assertThat(refreshToken).isEqualTo(existRefresh);
			Assertions.assertThat(refreshToken.getRefreshToken()).isEqualTo(newToken);
			Assertions.assertThat(bCryptPasswordEncoder.matches(newIp, refreshToken.getIp())).isTrue();
		}
	}

	@Nested
	@DisplayName("POST /token/refresh")
	class GetNewAccess {
		@Test
		@DisplayName("토큰이 유효하지 않을 경우 400을 반환한다.")
		void tokenNotExsist() throws Exception {
			//Arrange
			//Act
			//Assert
			mockMvc.perform(post("/token/refresh"))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("유저가 존재하지 않을 경우 400을 반환한다.")
		void userNotExist() throws Exception {
			//Arrange
			Cookie cookie = jwtUtil.createJwtCookie("username", "ROLE_ADMIN", JwtUtil.TokenType.REFRESH);

			//Act
			//Assert
			mockMvc.perform(post("/token/refresh")
					.cookie(cookie))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("DB에 저장된 토큰이 존재하지 않을 경우 400을 반환한다.")
		void dbTokenNotExist() throws Exception {
			//Arrange
			String username = "username";
			Cookie cookie = jwtUtil.createJwtCookie(username, "ROLE_ADMIN", JwtUtil.TokenType.REFRESH);

			Email email = Email.builder().email("email").build();
			User user = User.builder().email(email).username(username).password("password").isAdmin(true).build();
			em.persist(email);
			em.persist(user);
			em.flush();
			em.clear();

			//Act
			//Assert
			mockMvc.perform(post("/token/refresh")
					.cookie(cookie))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("DB에 저장된 토큰과 ip가 불일치할 경우 400을 반환한다.")
		void tokenIpNotMatch() throws Exception {
			//Arrange
			String username = "username";
			Cookie cookie = jwtUtil.createJwtCookie(username, "ROLE_ADMIN", JwtUtil.TokenType.REFRESH);
			String existIp = "existIp";
			String ip = "ip";

			Email email = Email.builder().email("email").build();
			User user = User.builder().username(username).email(email).password("password").isAdmin(true).build();
			em.persist(email);
			em.persist(user);
			RefreshToken refreshToken = createRefresh(cookie, user, existIp);
			em.persist(refreshToken);
			em.flush();
			em.clear();

			//Act
			//Assert
			mockMvc.perform(post("/token/refresh")
					.cookie(cookie)
					.header(X_FORWARDED_FOR_HEADER, ip))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("DB에 저장된 토큰과 토큰 정보가 불일치할 경우 400을 반환한다.")
		void tokenNotMatch() throws Exception {
			//Arrange
			String username = "username";
			Cookie existCookie = jwtUtil.createJwtCookie(username, "ROLE_ADMIN", JwtUtil.TokenType.REFRESH);
			sleep(1000);
			Cookie cookie = jwtUtil.createJwtCookie(username, "ROLE_ADMIN", JwtUtil.TokenType.REFRESH);
			String ip = "ip";

			Email email = Email.builder().email("email").build();
			User user = User.builder().username(username).email(email).password("password").isAdmin(true).build();
			em.persist(email);
			em.persist(user);
			RefreshToken refreshToken = createRefresh(existCookie, user, ip);
			em.persist(refreshToken);
			em.flush();
			em.clear();

			//Act
			//Assert
			mockMvc.perform(post("/token/refresh")
					.cookie(cookie)
					.header(X_FORWARDED_FOR_HEADER, ip))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("DB에 저장된 토큰과 ip, 토큰 정보가 일치할 경우 200을 반환한다.")
		void tokenMatch() throws Exception {
			//Arrange
			String username = "username";
			Cookie cookie = jwtUtil.createJwtCookie(username, "ROLE_ADMIN", JwtUtil.TokenType.REFRESH);
			String ip = "ip";

			Email email = Email.builder().email("email").build();
			User user = User.builder().username(username).email(email).password("password").isAdmin(true).build();
			em.persist(email);
			em.persist(user);
			RefreshToken refreshToken = createRefresh(cookie, user, ip);
			em.persist(refreshToken);
			em.flush();
			em.clear();

			//Act
			//Assert
			mockMvc.perform(post("/token/refresh")
					.cookie(cookie)
					.header(X_FORWARDED_FOR_HEADER, ip))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		}
	}

	public RefreshToken createRefresh(Cookie cookie, User user, String ip) {
		return RefreshToken.builder()
			.refreshToken(cookie.getValue())
			.user(user)
			.ip(bCryptPasswordEncoder.encode(ip))
			.build();
	}
}
