package com.rmf.apiserverjava.service.impl;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.utils.JwtUtil;
import com.rmf.apiserverjava.repository.RefreshTokenRepository;
import com.rmf.apiserverjava.repository.UserRepository;
import com.rmf.apiserverjava.security.UserSession;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@UnitTest
class AuthServiceImplUnitTest {

	@Mock
	JwtUtil jwtUtil;

	@Mock
	RefreshTokenRepository refreshTokenRepository;

	@Mock
	UserRepository userRepository;

	@Mock
	HttpServletRequest request;

	@Mock
	HttpServletResponse response;

	@Mock
	UserSession userSession;

	@InjectMocks
	AuthServiceImpl authServiceImpl;

	@Nested
	@DisplayName("logout")
	class Logout {
		@Test
		@DisplayName("로그아웃 시 DB에 저장된 RefreshToken을 삭제하고 JWT 쿠키를 만료시킨다.")
		void logoutSuccess() {
			// Arrange
			String token = "token";
			String username = "nonexistent";
			User user = mock(User.class);
			when(jwtUtil.getAccessTokenFromCookies(any())).thenReturn(token);
			when(jwtUtil.getUsername(eq(token))).thenReturn(username);
			when(userRepository.findById(eq(username))).thenReturn(Optional.of(user));

			// Act
			authServiceImpl.logout(request, response);

			// Assert
			verify(refreshTokenRepository).deleteByUser(eq(user));
			verify(jwtUtil).expireAccessToken(eq(response));
			verify(jwtUtil).expireRefreshToken(eq(response));
		}

		@Test
		@DisplayName("로그아웃 시 유저가 없으면 JWT 쿠키만 만료시킨다.")
		void logoutSuccessWithExpireCookie() {
			// Arrange
			String token = "token";
			String username = "nonexistent";
			when(jwtUtil.getAccessTokenFromCookies(any())).thenReturn(token);
			when(jwtUtil.getUsername(eq(token))).thenReturn(username);
			when(userRepository.findById(eq(username))).thenReturn(Optional.empty());

			// Act
			authServiceImpl.logout(request, response);

			// Assert
			verify(jwtUtil).expireAccessToken(eq(response));
			verify(jwtUtil).expireRefreshToken(eq(response));
		}
	}
}
