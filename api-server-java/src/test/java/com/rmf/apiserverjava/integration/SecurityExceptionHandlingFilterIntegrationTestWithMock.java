package com.rmf.apiserverjava.integration;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmf.apiserverjava.config.annotation.IntegrationTestWithSecurity;
import com.rmf.apiserverjava.entity.users.Email;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.utils.JwtUtil;
import com.rmf.apiserverjava.security.UserSession;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;

@IntegrationTestWithSecurity
@AutoConfigureMockMvc
@Transactional
class SecurityExceptionHandlingFilterIntegrationTestWithMock {
	@Autowired
	EntityManager em;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtUtil jwtUtil;

	@MockBean
	private UserSession userSession;

	Cookie access;
	String tokenUsername;

	@BeforeEach
	void setUp() {
		tokenUsername = "tokenUser";
		Email email = Email.builder().email("email").isVerified(true).build();
		User user = new User(tokenUsername, "password", false, email);
		em.persist(email);
		em.persist(user);
		em.flush();
		access = jwtUtil.createJwtCookie(tokenUsername, "ROLE_USER", JwtUtil.TokenType.ACCESS);
	}

	@Nested
	@DisplayName("SecurityExceptionHandlingFilter")
	class SecurityExceptionHandlingFilter {
		@Test
		@DisplayName("RuntimeException 발생 시 500 내부 서버 오류로 처리된다")
		void handleRuntimeException() throws Exception {
			//Arrange
			when(userSession.isExpired(eq(tokenUsername))).thenThrow(new RuntimeException());

			//Act
			ResultActions perform = mockMvc.perform(get("/is_login").cookie(access));

			//Assert
			perform.andExpect(status().isInternalServerError());
		}
	}
}