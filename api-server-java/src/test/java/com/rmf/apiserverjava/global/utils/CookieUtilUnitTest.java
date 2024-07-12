package com.rmf.apiserverjava.global.utils;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.rmf.apiserverjava.config.annotation.UnitTest;

import jakarta.servlet.http.Cookie;

@UnitTest
class CookieUtilUnitTest {

	private String hostname = "localhost";
	private boolean secure = false;

	private CookieUtil cookieUtil;

	@BeforeEach
	void setUp() {
		cookieUtil = new CookieUtil(hostname, secure);
	}

	@Nested
	@DisplayName("getCookieValue")
	class GetCookieValue {
		@Test
		@DisplayName("key에 해당하는 쿠키가 존재할 때, 쿠키의 값을 반환한다.")
		void success() {
			//Arrange
			Cookie[] cookies = new Cookie[1];
			String key = "key";
			String value = "value";
			cookies[0] = new Cookie(key, value);

			//Act
			String result = cookieUtil.getCookieValue(cookies, key);

			//Assert
			assertThat(result).isEqualTo(value);
		}

		@Test
		@DisplayName("key에 해당하는 쿠키가 존재하지 않으면 null을 반환한다.")
		void failed() {
			//Arrange
			Cookie[] cookies = new Cookie[1];
			String key = "key";
			String value = "value";
			cookies[0] = new Cookie(key, value);
			String notExistKey = "notExistKey";

			//Act
			String result = cookieUtil.getCookieValue(cookies, notExistKey);

			//Assert
			assertThat(result).isEqualTo(null);
		}

		@Test
		@DisplayName("null을 전달받으면 null을 반환한다.")
		void failedByNull() {
			//Arrange
			//Act
			String result = cookieUtil.getCookieValue(null, "notExistKey");

			//Assert
			assertThat(result).isEqualTo(null);
		}
	}

	@Nested
	@DisplayName("createCookie")
	class CreateCookie {
		@Test
		@DisplayName("전달받은 파라미터로 쿠키를 생성한다.")
		void success() {
			//Arrange
			String key = "key";
			String value = "value";
			String path = "/";
			int maxAge = 600000;

			//Act
			Cookie result = cookieUtil.createCookie(key, value, path, maxAge);

			//Assert
			assertThat(result.getName()).isEqualTo(key);
			assertThat(result.getValue()).isEqualTo(value);
			assertThat(result.getPath()).isEqualTo(path);
			assertThat(result.getMaxAge()).isEqualTo(maxAge);
			assertThat(result.getDomain()).isEqualTo(hostname);
			assertThat(result.getSecure()).isEqualTo(secure);
		}
	}
}
