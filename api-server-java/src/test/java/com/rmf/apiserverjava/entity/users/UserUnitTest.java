package com.rmf.apiserverjava.entity.users;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.rmf.apiserverjava.config.annotation.UnitTest;

@UnitTest
class UserUnitTest {

	@Nested
	@DisplayName("User Build")
	class UserBuild {
		@Test
		@DisplayName("Builder를 통해 User 객체 생성에 성공한다")
		void buildEntityTest() {
			//Arrange
			//Act
			User user = new User.UserBuilder()
				.username("username")
				.password("password")
				.isAdmin(true)
				.build();
			//Assert
			assertNotNull(user);
		}
	}

	@Nested
	@DisplayName("User Method")
	class UserMethod {
		@Test
		@DisplayName("setPassword 메소드는 User의 password를 업데이트한다")
		void updatePasswordTest() {
			//Arrange
			User user = new User("username", "password", true, null);
			//Act
			user.setPassword("newPassword");
			//Assert
			assertEquals(user.getPassword(), "newPassword");
		}
	}
}
