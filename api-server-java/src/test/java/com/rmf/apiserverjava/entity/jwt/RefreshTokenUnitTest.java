package com.rmf.apiserverjava.entity.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.entity.users.User;

@UnitTest
class RefreshTokenUnitTest {
	@Nested
	@DisplayName("Constructor")
	class Constructor {
		@Test
		@DisplayName("전달받은 user, refreshToken, ip로 객체를 생성한다.")
		void constructorSuccess() {
			//Arrange
			User user = mock(User.class);
			String token = "refreshToken";
			String ip = "ip";

			//Act
			RefreshToken refreshToken = new RefreshToken(user, token, ip);

			//Assert
			assertThat(refreshToken.getUser()).isEqualTo(user);
			assertThat(refreshToken.getRefreshToken()).isEqualTo(token);
			assertThat(refreshToken.getIp()).isEqualTo(ip);
		}
	}

	@Nested
	@DisplayName("updateToken")
	class UpdateToken {
		@Test
		@DisplayName("전달받은 token, ip로 객체를 업데이트한다.")
		void constructorSuccess() {
			//Arrange
			User user = mock(User.class);
			String token = "refreshToken";
			String ip = "ip";
			RefreshToken refreshToken = new RefreshToken(user, token, ip);

			String newToken = "newToken";
			String newIp = "newIp";

			//Act
			refreshToken.updateToken(newToken, newIp);

			//Assert
			assertThat(refreshToken.getUser()).isEqualTo(user);
			assertThat(refreshToken.getRefreshToken()).isEqualTo(newToken);
			assertThat(refreshToken.getIp()).isEqualTo(newIp);
		}
	}
}
