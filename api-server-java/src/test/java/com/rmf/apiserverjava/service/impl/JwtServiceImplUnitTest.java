package com.rmf.apiserverjava.service.impl;

import static com.rmf.apiserverjava.service.impl.JwtServiceImpl.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.entity.jwt.RefreshToken;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.exception.custom.BusinessException;
import com.rmf.apiserverjava.global.utils.JwtUtil;
import com.rmf.apiserverjava.repository.RefreshTokenRepository;
import com.rmf.apiserverjava.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@UnitTest
class JwtServiceImplUnitTest {

	@Mock
	RefreshTokenRepository refreshTokenRepository;

	@Mock
	UserRepository userRepository;

	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Mock
	JwtUtil jwtUtil;

	@Mock
	HttpServletRequest request;

	@Mock
	HttpServletResponse response;

	@InjectMocks
	JwtServiceImpl jwtServiceImpl;

	@Nested
	@DisplayName("createOrUpdateRefreshToken")
	class CreateOrUpdateRefreshToken {
		@Test
		@DisplayName("사용자가 존재하지 않을 경우 BusinessException이 발생한다.")
		void userNotExist() {
			// Arrange
			String username = "nonexistent";
			when(userRepository.findById(username)).thenReturn(Optional.empty());

			// Act & Assert
			assertThrows(BusinessException.class, () -> {
				jwtServiceImpl.createOrUpdateRefreshToken("token", username, request);
			});
		}

		@Test
		@DisplayName("기존 토큰이 없고 xForwardedForHeader가 존재하면 첫번째 Ip로 새로운 RefreshToken을 생성한다.")
		void createsNewRefreshTokenByXForwardedWhenNoneExists() {
			// Arrange
			String username = "user1";
			User user = mock(User.class);
			String targetIp = "ip";
			when(userRepository.findById(username)).thenReturn(Optional.of(user));
			when(refreshTokenRepository.findByUser(any(User.class))).thenReturn(Optional.empty());
			when(request.getHeader(eq(X_FORWARDED_FOR_HEADER))).thenReturn(targetIp + ", anotherIp");

			when(bCryptPasswordEncoder.encode(any(String.class))).thenReturn("hashEncodedIp");

			// Act
			jwtServiceImpl.createOrUpdateRefreshToken("newToken", username, request);

			// Assert
			verify(refreshTokenRepository).save(any(RefreshToken.class));
			verify(bCryptPasswordEncoder).encode(eq(targetIp));
		}

		@Test
		@DisplayName("기존 토큰이 없고 xForwardedForHeader가 존재하지 않으면 getRemoteAddr Ip로 새로운 RefreshToken을 생성한다.")
		void createsNewRefreshTokenByRemoteAddrWhenNoneExists() {
			// Arrange
			String username = "user1";
			User user = mock(User.class);
			String remoteIp = "ip";
			when(userRepository.findById(username)).thenReturn(Optional.of(user));
			when(refreshTokenRepository.findByUser(any(User.class))).thenReturn(Optional.empty());
			when(request.getHeader(eq(X_FORWARDED_FOR_HEADER))).thenReturn(null);
			when(request.getRemoteAddr()).thenReturn(remoteIp);
			when(bCryptPasswordEncoder.encode(any(String.class))).thenReturn("hashEncodedIp");

			// Act
			jwtServiceImpl.createOrUpdateRefreshToken("newToken", username, request);

			// Assert
			verify(refreshTokenRepository).save(any(RefreshToken.class));
			verify(request).getRemoteAddr();
			verify(bCryptPasswordEncoder).encode(eq(remoteIp));
		}

		@Test
		@DisplayName("기존 토큰이 존재한다면 RefreshToken을 업데이트한다.")
		void updateRefreshTokenWhenExists() {
			// Arrange
			String username = "user1";
			User user = mock(User.class);
			RefreshToken refreshToken = mock(RefreshToken.class);
			String remoteIp = "ip";
			when(userRepository.findById(username)).thenReturn(Optional.of(user));
			when(refreshTokenRepository.findByUser(eq(user))).thenReturn(Optional.of(refreshToken));
			when(request.getHeader(eq(X_FORWARDED_FOR_HEADER))).thenReturn(null);
			when(request.getRemoteAddr()).thenReturn(remoteIp);
			when(bCryptPasswordEncoder.encode(any(String.class))).thenReturn("hashEncodedIp");

			// Act
			jwtServiceImpl.createOrUpdateRefreshToken("newToken", username, request);

			// Assert
			verify(refreshToken).updateToken(any(String.class), any(String.class));
			verify(request).getRemoteAddr();
			verify(bCryptPasswordEncoder).encode(eq(remoteIp));
		}
	}

	@Nested
	@DisplayName("renewAccessTokenCookie")
	class RenewAccessTokenCookie {
		@Test
		@DisplayName("토큰이 유효하지 않다면 토큰을 만료시키고 Optional.empty를 반환한다.")
		void invalidToken() {
			// Arrange
			String refreshToken = "invalidToken";

			when(jwtUtil.getRefreshTokenFromCookies(any())).thenReturn(refreshToken);
			when(jwtUtil.isValidToken(eq(refreshToken), eq(JwtUtil.TokenType.REFRESH))).thenReturn(false);

			// Act
			Optional<Cookie> cookie = jwtServiceImpl.renewAccessTokenCookie(request, response);

			// Assert
			verify(jwtUtil).expireRefreshToken(eq(response));
			Assertions.assertThat(cookie).isEmpty();
		}

		@Test
		@DisplayName("유저가 존재하지 않다면 토큰을 만료시키고 Optional.empty를 반환한다.")
		void userNotExist() {
			// Arrange
			String refreshToken = "validToken";
			User user = mock(User.class);

			when(jwtUtil.getRefreshTokenFromCookies(any())).thenReturn(refreshToken);
			when(jwtUtil.isValidToken(eq(refreshToken), eq(JwtUtil.TokenType.REFRESH))).thenReturn(true);
			when(userRepository.findById(any())).thenReturn(Optional.empty());

			// Act
			Optional<Cookie> cookie = jwtServiceImpl.renewAccessTokenCookie(request, response);

			// Assert
			verify(jwtUtil).expireRefreshToken(eq(response));
			Assertions.assertThat(cookie).isEmpty();
		}

		@Test
		@DisplayName("저장된 토큰이 존재하지 않다면 토큰을 만료시키고 Optional.empty를 반환한다.")
		void savedTokenNotExist() {
			// Arrange
			String refreshToken = "validToken";
			User user = mock(User.class);

			when(jwtUtil.getRefreshTokenFromCookies(any())).thenReturn(refreshToken);
			when(jwtUtil.isValidToken(eq(refreshToken), eq(JwtUtil.TokenType.REFRESH))).thenReturn(true);
			when(userRepository.findById(any())).thenReturn(Optional.of(user));
			when(refreshTokenRepository.findByUser(eq(user))).thenReturn(Optional.empty());

			// Act
			Optional<Cookie> cookie = jwtServiceImpl.renewAccessTokenCookie(request, response);

			// Assert
			verify(jwtUtil).expireRefreshToken(eq(response));
			Assertions.assertThat(cookie).isEmpty();
		}

		@Test
		@DisplayName("저장된 토큰이 일치하지 않다면 토큰을 만료시키고 Optional.empty를 반환한다.")
		void savedTokenNotMatch() {
			// Arrange
			String token = "validToken";
			String savedToken = token + "different";
			User user = mock(User.class);
			RefreshToken refreshToken = mock(RefreshToken.class);

			when(jwtUtil.getRefreshTokenFromCookies(any())).thenReturn(token);
			when(jwtUtil.isValidToken(eq(token), eq(JwtUtil.TokenType.REFRESH))).thenReturn(true);
			when(userRepository.findById(any())).thenReturn(Optional.of(user));
			when(refreshTokenRepository.findByUser(eq(user))).thenReturn(Optional.of(refreshToken));
			when(refreshToken.getRefreshToken()).thenReturn(savedToken);

			// Act
			Optional<Cookie> cookie = jwtServiceImpl.renewAccessTokenCookie(request, response);

			// Assert
			verify(jwtUtil).expireRefreshToken(eq(response));
			Assertions.assertThat(cookie).isEmpty();
		}

		@Test
		@DisplayName("저장된 토큰의 IP가 일치하지 않다면 토큰을 만료시키고 Optional.empty를 반환한다.")
		void savedTokenIpNotMatch() {
			// Arrange
			String token = "validToken";
			String savedToken = token;
			User user = mock(User.class);
			RefreshToken refreshToken = mock(RefreshToken.class);

			when(jwtUtil.getRefreshTokenFromCookies(any())).thenReturn(token);
			when(jwtUtil.isValidToken(eq(token), eq(JwtUtil.TokenType.REFRESH))).thenReturn(true);
			when(userRepository.findById(any())).thenReturn(Optional.of(user));
			when(refreshTokenRepository.findByUser(eq(user))).thenReturn(Optional.of(refreshToken));
			when(refreshToken.getRefreshToken()).thenReturn(savedToken);
			when(bCryptPasswordEncoder.matches(any(), any())).thenReturn(false);

			// Act
			Optional<Cookie> cookie = jwtServiceImpl.renewAccessTokenCookie(request, response);

			// Assert
			verify(jwtUtil).expireRefreshToken(eq(response));
			Assertions.assertThat(cookie).isEmpty();
		}

		@Test
		@DisplayName("저장된 토큰이 일치한다면 새로운 accessToken을 반환한다.")
		void savedTokenMatch() {
			// Arrange
			String token = "validToken";
			String savedToken = token;
			User user = mock(User.class);
			RefreshToken refreshToken = mock(RefreshToken.class);

			when(jwtUtil.getRefreshTokenFromCookies(any())).thenReturn(token);
			when(jwtUtil.isValidToken(eq(token), eq(JwtUtil.TokenType.REFRESH))).thenReturn(true);
			when(userRepository.findById(any())).thenReturn(Optional.of(user));
			when(refreshTokenRepository.findByUser(eq(user))).thenReturn(Optional.of(refreshToken));
			when(refreshToken.getRefreshToken()).thenReturn(savedToken);
			when(bCryptPasswordEncoder.matches(any(), any())).thenReturn(true);
			when(jwtUtil.createJwtCookie(any(), any(), eq(JwtUtil.TokenType.ACCESS))).thenReturn(mock(Cookie.class));

			// Act
			Optional<Cookie> cookie = jwtServiceImpl.renewAccessTokenCookie(request, response);

			// Assert
			Assertions.assertThat(cookie).isNotEmpty();
		}

		@Test
		@DisplayName("기존 토큰이 존재한다면 RefreshToken을 업데이트한다.")
		void updateRefreshTokenWhenExists() {
			// Arrange
			String username = "user1";
			User user = mock(User.class);
			RefreshToken refreshToken = mock(RefreshToken.class);
			String remoteIp = "ip";
			when(userRepository.findById(username)).thenReturn(Optional.of(user));
			when(refreshTokenRepository.findByUser(eq(user))).thenReturn(Optional.of(refreshToken));
			when(request.getHeader(eq(X_FORWARDED_FOR_HEADER))).thenReturn(null);
			when(request.getRemoteAddr()).thenReturn(remoteIp);
			when(bCryptPasswordEncoder.encode(any(String.class))).thenReturn("hashEncodedIp");

			// Act
			jwtServiceImpl.createOrUpdateRefreshToken("newToken", username, request);

			// Assert
			verify(refreshToken).updateToken(any(String.class), any(String.class));
			verify(request).getRemoteAddr();
			verify(bCryptPasswordEncoder).encode(eq(remoteIp));
		}
	}
}
