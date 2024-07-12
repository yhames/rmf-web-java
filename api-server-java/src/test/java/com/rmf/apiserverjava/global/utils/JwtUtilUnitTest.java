package com.rmf.apiserverjava.global.utils;

import static java.lang.Thread.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.global.exception.custom.BusinessException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@UnitTest
class JwtUtilUnitTest {

	String secret = "secretofJwtUtilUnitTest1234567890!@#$%^&*()";
	Long accessExpiredMs = 1000000L;
	Long refreshExpiredMs = 1000000L;
	String accessTokenName = "accessToken";
	String refreshTokenName = "refreshToken";

	@Mock
	CookieUtil cookieUtil;
	JwtUtil jwtUtil;

	@BeforeEach
	void setUp() {
		jwtUtil = new JwtUtil(secret, accessExpiredMs, refreshExpiredMs, accessTokenName, refreshTokenName, cookieUtil);
	}

	@Nested
	@DisplayName("getUsername")
	class GetUsername {
		@Test
		@DisplayName("토큰에서 유저네임을 추출한다")
		void success() {
			//Arrange
			String username = "username";
			String jwt = jwtUtil.createJwt(username, "role", JwtUtil.TokenType.ACCESS);

			//Act
			String result = jwtUtil.getUsername(jwt);

			//Assert
			assertThat(result).isEqualTo(username);
		}
	}

	@Nested
	@DisplayName("getRole")
	class GetRole {
		@Test
		@DisplayName("토큰에서 Role을 추출한다")
		void success() {
			//Arrange
			String role = "role";
			String jwt = jwtUtil.createJwt("username", role, JwtUtil.TokenType.ACCESS);

			//Act
			String result = jwtUtil.getRole(jwt);

			//Assert
			assertThat(result).isEqualTo(role);
		}
	}

	@Nested
	@DisplayName("getTokenType")
	class GetTokenType {
		@Test
		@DisplayName("토큰에서 TokenType을 String으로 추출한다")
		void success() {
			//Arrange
			JwtUtil.TokenType tokenType = JwtUtil.TokenType.ACCESS;
			String jwt = jwtUtil.createJwt("username", "role", tokenType);

			//Act
			String result = jwtUtil.getTokenType(jwt);

			//Assert
			assertThat(result).isEqualTo(tokenType.name());
		}
	}

	@Nested
	@DisplayName("isExpired")
	class IsExpired {
		@Test
		@DisplayName("토큰이 만료되지 않았다면 false를 반환한다")
		void isNotExpired() {
			//Arrange
			String jwt = jwtUtil.createJwt("username", "role", JwtUtil.TokenType.ACCESS);

			//Act
			boolean result = jwtUtil.isExpired(jwt);

			//Assert
			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("토큰이 만료되었다면 true를 반환한다")
		void isExpired() {
			//Arrange
			JwtUtil expireJwtUtil;
			expireJwtUtil = new JwtUtil(secret, 0L, refreshExpiredMs, accessTokenName, refreshTokenName,
				cookieUtil);
			String jwt = expireJwtUtil.createJwt("username", "role", JwtUtil.TokenType.ACCESS);

			//Act
			boolean result = expireJwtUtil.isExpired(jwt);

			//Assert
			assertThat(result).isTrue();
		}
	}

	@Nested
	@DisplayName("CreateJwtCookie")
	class CreateJwtCookie {
		@Test
		@DisplayName("전달받은 username, role, TokenType.ACCESS로 Access jwt 쿠키를 생성한다")
		void createAccessCookie() {
			//Arrange
			when(cookieUtil.createCookie(eq(accessTokenName), any(String.class), eq("/"), any(Integer.class)))
				.thenReturn(mock(Cookie.class));

			//Act
			Cookie result = jwtUtil.createJwtCookie("username", "role", JwtUtil.TokenType.ACCESS);

			//Assert
			assertThat(result).isNotNull();
			verify(cookieUtil).createCookie(eq(accessTokenName), any(String.class), eq("/"), any(Integer.class));
		}

		@Test
		@DisplayName("전달받은 username, role, TokenType.REFRESH로 Refresh jwt 쿠키를 생성한다")
		void createRefreshCookie() {
			//Arrange
			when(cookieUtil.createCookie(eq(refreshTokenName), any(String.class), eq("/token/refresh"),
				any(Integer.class))).thenReturn(mock(Cookie.class));

			//Act
			Cookie result = jwtUtil.createJwtCookie("username", "role", JwtUtil.TokenType.REFRESH);

			//Assert
			assertThat(result).isNotNull();
			verify(cookieUtil).createCookie(eq(refreshTokenName), any(String.class), eq("/token/refresh"),
				any(Integer.class));
		}
	}

	@Nested
	@DisplayName("verify")
	class Verify {
		@Test
		@DisplayName("동일한 시그니처 토큰이 아니면 예외를 발생시킨다")
		void invalidSecret() {
			//Arrange
			JwtUtil differentSecretUtil = new JwtUtil(secret + "different", accessExpiredMs, refreshExpiredMs,
				accessTokenName, refreshTokenName, cookieUtil);
			String jwt = differentSecretUtil.createJwt("username", "role", JwtUtil.TokenType.ACCESS);

			//Act
			//Assert
			assertThrows(io.jsonwebtoken.security.SignatureException.class, () -> jwtUtil.getUsername(jwt));
		}

		@Test
		@DisplayName("유효하지 않은 토큰 타입이면 BusinessException이 발생한다")
		void invalidType() {
			//Arrange
			//Act
			//Assert
			assertThrows(BusinessException.class,
				() -> jwtUtil.createJwt("username", "role", null));
		}
	}

	@Nested
	@DisplayName("getTokenFromCookies")
	class GetAccessTokenFromCookies {
		@Test
		@DisplayName("쿠키로부터 AccessToken을 추출한다")
		void success() {
			//Arrange
			Cookie[] cookies = new Cookie[1];

			//Act
			//Assert
			assertDoesNotThrow(() -> jwtUtil.getAccessTokenFromCookies(cookies));
		}
	}

	@Nested
	@DisplayName("getRefreshTokenFromCookies")
	class GetRefreshTokenFromCookies {
		@Test
		@DisplayName("쿠키로부터 RefrehToken을 추출한다")
		void success() {
			//Arrange
			Cookie[] cookies = new Cookie[1];

			//Act
			//Assert
			assertDoesNotThrow(() -> jwtUtil.getRefreshTokenFromCookies(cookies));
		}
	}

	@Nested
	@DisplayName("expireAccessToken")
	class ExpireAccessToken {
		@Test
		@DisplayName("즉시 만료되는 AccessToken 쿠키를 생성해 response에 추가한다")
		void success() {
			//Arrange
			HttpServletResponse response = mock(HttpServletResponse.class);
			when(cookieUtil.createCookie(eq(accessTokenName), eq(""), eq("/"), eq(0)))
				.thenReturn(mock(Cookie.class));

			//Act
			jwtUtil.expireAccessToken(response);

			//Assert
			verify(response).addCookie(any(Cookie.class));
		}
	}

	@Nested
	@DisplayName("expireRefreshToken")
	class ExpireRefreshToken {
		@Test
		@DisplayName("즉시 만료되는 RefreshToken 쿠키를 생성해 response에 추가한다")
		void success() {
			//Arrange
			HttpServletResponse response = mock(HttpServletResponse.class);
			when(cookieUtil.createCookie(eq(refreshTokenName), eq(""), eq("/token/refresh"), eq(0)))
				.thenReturn(mock(Cookie.class));

			//Act
			jwtUtil.expireRefreshToken(response);

			//Assert
			verify(response).addCookie(any(Cookie.class));
		}
	}

	@Nested
	@DisplayName("isAccessToken")
	class IsAccessToken {
		@Test
		@DisplayName("AccessToken이면 true를 반환한다")
		void isAccessToken() {
			//Arrange
			JwtUtil.TokenType tokenType = JwtUtil.TokenType.ACCESS;
			String jwt = jwtUtil.createJwt("username", "role", tokenType);

			//Act
			boolean result = jwtUtil.isAccessToken(jwt);

			//Assert
			assertThat(result).isTrue();
		}

		@Test
		@DisplayName("AccessToken이 아니면 false를 반환한다")
		void isNotAccessToken() {
			//Arrange
			JwtUtil.TokenType tokenType = JwtUtil.TokenType.REFRESH;
			String jwt = jwtUtil.createJwt("username", "role", tokenType);

			//Act
			boolean result = jwtUtil.isAccessToken(jwt);

			//Assert
			assertThat(result).isFalse();
		}
	}

	@Nested
	@DisplayName("isRefreshToken")
	class IsRefreshToken {
		@Test
		@DisplayName("RefreshToken이면 true를 반환한다")
		void isRefreshToken() {
			//Arrange
			JwtUtil.TokenType tokenType = JwtUtil.TokenType.REFRESH;
			String jwt = jwtUtil.createJwt("username", "role", tokenType);

			//Act
			boolean result = jwtUtil.isRefreshToken(jwt);

			//Assert
			assertThat(result).isTrue();
		}

		@Test
		@DisplayName("RefreshToken이 아니면 false를 반환한다")
		void isNotRefreshToken() {
			//Arrange
			JwtUtil.TokenType tokenType = JwtUtil.TokenType.ACCESS;
			String jwt = jwtUtil.createJwt("username", "role", tokenType);

			//Act
			boolean result = jwtUtil.isRefreshToken(jwt);

			//Assert
			assertThat(result).isFalse();
		}
	}

	@Nested
	@DisplayName("getTokenExpirationTime")
	class GetTokenExpirationTime {
		@Test
		@DisplayName("만료되었으면 0을 반환한다")
		void isExpired() {
			//Arrange
			JwtUtil expireJwtUtil;
			expireJwtUtil = new JwtUtil(secret, 0L, refreshExpiredMs, accessTokenName, refreshTokenName,
				cookieUtil);
			String jwt = expireJwtUtil.createJwt("username", "role", JwtUtil.TokenType.ACCESS);

			//Act
			Long result = expireJwtUtil.getTokenExpirationTime(jwt);

			//Assert
			assertThat(result).isEqualTo(0);
		}

		@Test
		@DisplayName("만료되지 않았다면 만료까지 남은 시간을 반환한다")
		void isNotExpired() {
			//Arrange
			String jwt = jwtUtil.createJwt("username", "role", JwtUtil.TokenType.ACCESS);

			//Act
			Long result = jwtUtil.getTokenExpirationTime(jwt);

			//Assert
			assertThat(result).isLessThanOrEqualTo(accessExpiredMs);
		}
	}

	@Nested
	@DisplayName("getTokenExpirationTime")
	class IsValidToken {
		@Test
		@DisplayName("유효한 토큰일 경우 true를 반환한다")
		void success() {
			//Arrange
			JwtUtil.TokenType tokenType = JwtUtil.TokenType.ACCESS;
			String jwt = jwtUtil.createJwt("username", "role", tokenType);

			//Act
			boolean result = jwtUtil.isValidToken(jwt, tokenType);

			//Assert
			assertThat(result).isTrue();
		}

		@Test
		@DisplayName("Refresh 토큰에 대해 타입을 Refresh로 전달할 경우 true를 반환한다")
		void successBySameRefreshType() {
			//Arrange
			JwtUtil.TokenType tokenType = JwtUtil.TokenType.REFRESH;
			String jwt = jwtUtil.createJwt("username", "role", tokenType);

			//Act
			boolean result = jwtUtil.isValidToken(jwt, JwtUtil.TokenType.REFRESH);

			//Assert
			assertThat(result).isTrue();
		}

		@Test
		@DisplayName("토큰이 Null일 경우 false를 반환한다")
		void failByNullToken() {
			//Arrange
			JwtUtil.TokenType tokenType = JwtUtil.TokenType.ACCESS;

			//Act
			boolean result = jwtUtil.isValidToken(null, tokenType);

			//Assert
			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("토큰이 만료되었을 경우 false를 반환한다")
		void failByExpireToken() {
			//Arrange
			JwtUtil.TokenType tokenType = JwtUtil.TokenType.ACCESS;
			JwtUtil expireJwtUtil;
			expireJwtUtil = new JwtUtil(secret, 0L, refreshExpiredMs, accessTokenName, refreshTokenName,
				cookieUtil);
			String jwt = expireJwtUtil.createJwt("username", "role", tokenType);

			//Act
			boolean result = jwtUtil.isValidToken(jwt, tokenType);

			//Assert
			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("토큰 타입이 처리되지 않는 값일 경우 false를 반환한다")
		void failByUnexpectedType() {
			//Arrange
			JwtUtil.TokenType tokenType = JwtUtil.TokenType.ACCESS;
			String jwt = jwtUtil.createJwt("username", "role", tokenType);

			//Act
			boolean result = jwtUtil.isValidToken(jwt, null);

			//Assert
			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("access 토큰에 대해 타입을 refresh로 전달할 경우 false를 반환한다")
		void failByDifferentAccessType() {
			//Arrange
			JwtUtil.TokenType tokenType = JwtUtil.TokenType.ACCESS;
			String jwt = jwtUtil.createJwt("username", "role", tokenType);

			//Act
			boolean result = jwtUtil.isValidToken(jwt, JwtUtil.TokenType.REFRESH);

			//Assert
			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("refresh 토큰에 대해 타입을 access로 전달할 경우 false를 반환한다")
		void failByDifferentRefreshType() {
			//Arrange
			JwtUtil.TokenType tokenType = JwtUtil.TokenType.REFRESH;
			String jwt = jwtUtil.createJwt("username", "role", tokenType);

			//Act
			boolean result = jwtUtil.isValidToken(jwt, JwtUtil.TokenType.ACCESS);

			//Assert
			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("동일한 시그니처 토큰이 아니면 false를 반환한다")
		void invalidSecret() {
			//Arrange
			JwtUtil differentSecretUtil = new JwtUtil(secret + "different", accessExpiredMs, refreshExpiredMs,
				accessTokenName, refreshTokenName, cookieUtil);
			String jwt = differentSecretUtil.createJwt("username", "role", JwtUtil.TokenType.ACCESS);

			//Act
			boolean result = jwtUtil.isValidToken(jwt, JwtUtil.TokenType.ACCESS);

			//Assert
			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("유저별 최종 비활성화 이후 토큰이면 false를 반환한다")
		void inDormantAccess() throws InterruptedException {
			//Arrange
			String username = UUID.randomUUID().toString();
			JwtUtil.TokenType tokenType = JwtUtil.TokenType.ACCESS;
			String jwt = jwtUtil.createJwt(username, "role", tokenType);

			sleep(1000);
			jwtUtil.renewDormantTime(username);

			//Act
			boolean result = jwtUtil.isValidToken(jwt, tokenType);

			//Assert
			assertThat(result).isFalse();
		}
	}

	@Nested
	@DisplayName("getAccessTokenFromHeader")
	class GetAccessTokenFromHeader {

		@Test
		@DisplayName("쿠키 헤더에서 액세스 토큰 추출을 수행한다")
		void success() {
			// Arrange
			String token = "abc123";
			String cookieHeader =
				"otherCookie=someValue; " + accessTokenName + "=" + token + "; anotherCookie=anotherValue";

			// Act
			String result = jwtUtil.getAccessTokenFromHeader(cookieHeader);

			// Assert
			assertThat(result).isEqualTo(token);
		}

		@Test
		@DisplayName("쿠키 헤더에 액세스 토큰이 없으면 null을 반환한다")
		void fail() {
			// Arrange
			String cookieHeader = "anotherCookie=anotherValue";

			// Act
			String result = jwtUtil.getAccessTokenFromHeader(cookieHeader);

			// Assert
			assertThat(result).isEqualTo(null);
		}

		@Test
		@DisplayName("null을 인자로 전달할 경우 null을 반환한다")
		void failByNull() {
			// Arrange
			// Act
			String result = jwtUtil.getAccessTokenFromHeader(null);

			// Assert
			assertThat(result).isEqualTo(null);
		}
	}

	@Nested
	@DisplayName("accessGenerateTimeMills")
	class AccessGenerateTimeMills {

		@Test
		@DisplayName("토큰의 남은 시간을 반환한다")
		void success() {
			//Arrange
			JwtUtil.TokenType tokenType = JwtUtil.TokenType.ACCESS;
			String jwt = jwtUtil.createJwt("username", "role", tokenType);

			//Act
			long result = jwtUtil.accessGenerateTimeMills(jwt);

			//Assert
			assertThat(result).isNotEqualTo(0);
		}

		@Test
		@DisplayName("유효하지 않은 토큰이면 0을 반환한다")
		void notValid() {
			//Arrange
			JwtUtil differentSecretUtil = new JwtUtil(secret + "different", accessExpiredMs, refreshExpiredMs,
				accessTokenName, refreshTokenName, cookieUtil);
			String jwt = differentSecretUtil.createJwt("username", "role", JwtUtil.TokenType.ACCESS);

			//Act
			long result = jwtUtil.accessGenerateTimeMills(jwt);

			//Assert
			assertThat(result).isEqualTo(0);
		}
	}
}
