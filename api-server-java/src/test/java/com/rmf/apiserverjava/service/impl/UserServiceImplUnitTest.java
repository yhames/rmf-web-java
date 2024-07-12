package com.rmf.apiserverjava.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.dto.users.ChangePwReqDto;
import com.rmf.apiserverjava.dto.users.CreateUserReqDto;
import com.rmf.apiserverjava.dto.users.UserQueryRequestDto;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.exception.custom.BusinessException;
import com.rmf.apiserverjava.global.exception.custom.NotFoundException;
import com.rmf.apiserverjava.global.utils.JwtUtil;
import com.rmf.apiserverjava.repository.EmailRepository;
import com.rmf.apiserverjava.repository.UserQueryRepository;
import com.rmf.apiserverjava.repository.UserRepository;
import com.rmf.apiserverjava.security.UserSession;

@UnitTest
class UserServiceImplUnitTest {

	@Mock
	UserRepository userRepository;

	@Mock
	UserQueryRepository userQueryRepository;

	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Mock
	UserSession userSession;

	@Mock
	JwtUtil jwtUtil;

	@Mock
	EmailRepository emailRepository;

	@InjectMocks
	UserServiceImpl userServiceImpl;

	@Nested
	@DisplayName("getAllUsers")
	class GetAllUsers {

		@Test
		@DisplayName("유저가 존재할 경우 유저 목록을 반환한다.")
		void success() {
			// Arrange
			User user = User.builder().build();
			when(userQueryRepository.findAllUserByQuery(any(), any(), any()))
				.thenReturn(List.of(user));

			UserQueryRequestDto userQueryRequestDto
				= UserQueryRequestDto.builder().build();

			// Act
			List<User> allUsers = userServiceImpl.getAllUsers(userQueryRequestDto);

			// Assert
			assertThat(allUsers.size()).isEqualTo(1);
		}

		@Test
		@DisplayName("유저가 존재하지 않을 경우 빈 리스트를 반환한다.")
		void noUser() {
			// Arrange
			when(userQueryRepository.findAllUserByQuery(any(), any(), any()))
				.thenReturn(new ArrayList<>());

			UserQueryRequestDto userQueryRequestDto
				= UserQueryRequestDto.builder().build();

			// Act
			List<User> allUsers = userServiceImpl.getAllUsers(userQueryRequestDto);

			// Assert
			assertThat(allUsers.size()).isEqualTo(0);
		}
	}

	@Nested
	@DisplayName("getUser")
	class GetUser {

		@Test
		@DisplayName("존재하는 유저일 경우 유저를 반환한다.")
		void success() {
			// Arrange
			String userId = "test";
			User user = User.builder().build();
			when(userRepository.findById(eq(userId)))
				.thenReturn(Optional.of(user));

			// Act
			Optional<User> optionalUser = userServiceImpl.getUser(userId);

			// Assert
			assertThat(optionalUser).isPresent();
		}

		@Test
		@DisplayName("존재하지 않는 유저일 경우 빈 Optional을 반환한다.")
		void noUser() {
			// Arrange
			String userId = "test";
			when(userRepository.findById(eq(userId)))
				.thenReturn(Optional.empty());

			// Act
			Optional<User> optionalUser = userServiceImpl.getUser(userId);

			// Assert
			assertThat(optionalUser).isEmpty();
		}
	}

	@Nested
	@DisplayName("createUser")
	class CreateUser {

		@Test
		@DisplayName("유저 생성에 성공한다.")
		void success() {
			// Arrange
			CreateUserReqDto createUserReqDto = CreateUserReqDto.builder()
				.username("test")
				.email("email")
				.build();
			when(bCryptPasswordEncoder.encode(any()))
				.thenReturn("password");
			when(userRepository.existsById(any()))
				.thenReturn(false);
			when(emailRepository.existsByEmail(any()))
				.thenReturn(false);
			when(userRepository.save(any()))
				.thenReturn(User.builder()
					.username(createUserReqDto.getUsername())
					.password("password")
					.build());
			// Act
			User user = userServiceImpl.createUser(createUserReqDto);

			// Assert
			assertThat(user).isNotNull();
			assertThat(user.getUsername()).isEqualTo("test");
		}

		@Test
		@DisplayName("이미 존재하는 아이디일 경우 예외를 던진다.")
		void alreadyExistsId() {
			// Arrange
			CreateUserReqDto createUserReqDto = CreateUserReqDto.builder()
				.username("test")
				.email("email")
				.build();
			when(userRepository.existsById(any()))
				.thenReturn(true);

			// Act & Assert
			assertThrows(IllegalArgumentException.class, () -> {
				userServiceImpl.createUser(createUserReqDto);
			});
		}

		@Test
		@DisplayName("이미 존재하는 이메일일 경우 예외를 던진다.")
		void alreadyExistsEmail() {
			// Arrange
			CreateUserReqDto createUserReqDto = CreateUserReqDto.builder()
				.username("test")
				.email("email")
				.build();
			when(userRepository.existsById(any()))
				.thenReturn(false);
			when(emailRepository.existsByEmail(any()))
				.thenReturn(true);

			// Act & Assert
			assertThrows(IllegalArgumentException.class, () -> {
				userServiceImpl.createUser(createUserReqDto);
			});
		}

		@Test
		@DisplayName("유저 저장에 실패했을 경우 예외를 던진다.")
		void saveFailed() {
			// Arrange
			CreateUserReqDto createUserReqDto = CreateUserReqDto.builder()
				.username("test")
				.email("email")
				.build();
			when(bCryptPasswordEncoder.encode(any()))
				.thenReturn("password");
			when(userRepository.existsById(any()))
				.thenReturn(false);
			when(emailRepository.existsByEmail(any()))
				.thenReturn(false);
			when(userRepository.save(any()))
				.thenThrow(new RuntimeException());
			// Act & Assert
			assertThrows(BusinessException.class, () -> {
				userServiceImpl.createUser(createUserReqDto);
			});
		}
	}

	@Nested
	@DisplayName("deleteUser")
	class DeleteUser {

		@Test
		@DisplayName("존재하는 유저의 경우 유저 삭제에 성공한다.")
		void success() {
			// Arrange
			String userId = "test";
			User user = User.builder().build();
			when(userRepository.findById(eq(userId)))
				.thenReturn(Optional.of(user));
			// Act
			userServiceImpl.deleteUser(userId);
			// Assert
			verify(userRepository, times(1)).delete(eq(user));
		}

		@Test
		@DisplayName("존재하지 않는 유저일 경우 예외를 던진다.")
		void noUser() {
			// Arrange
			String userId = "test";
			when(userRepository.findById(eq(userId)))
				.thenReturn(Optional.empty());
			// Act & Assert
			assertThrows(NotFoundException.class, () -> {
				userServiceImpl.deleteUser(userId);
			});
		}
	}

	@Nested
	@DisplayName("changePassword")
	class ChangePassword {

		@Test
		@DisplayName("비밀번호 변경에 성공한다.")
		void success() {
			// Arrange
			String username = "test";
			String oldPassword = "old";
			String newPassword = "new";
			String confirmPassword = "new";
			ChangePwReqDto changePwReqDto = mock(ChangePwReqDto.class);
			when(changePwReqDto.getCurrentPassword()).thenReturn(oldPassword);
			when(changePwReqDto.getNewPassword()).thenReturn(newPassword);
			when(changePwReqDto.getConfirmPassword()).thenReturn(confirmPassword);
			User userReturn = mock(User.class);
			when(bCryptPasswordEncoder.matches(any(), any())).thenReturn(true);
			when(bCryptPasswordEncoder.encode(any())).thenReturn("encoded");
			when(userReturn.getPassword()).thenReturn("old");
			when(userReturn.getPassword()).thenReturn("encoded");
			when(userRepository.findById(eq(username)))
				.thenReturn(Optional.of(userReturn));
			// Act & Assert
			userServiceImpl.changePassword(username, changePwReqDto);

		}

		@Test
		@DisplayName("존재하지 않는 유저일 경우 예외를 던진다.")
		void noUser() {
			// Arrange
			String userId = "test";
			ChangePwReqDto changePwReqDto = ChangePwReqDto.builder()
				.currentPassword("old")
				.newPassword("new")
				.confirmPassword("new")
				.build();
			when(userRepository.findById(eq(userId)))
				.thenReturn(Optional.empty());
			// Act & Assert
			assertThrows(NotFoundException.class, () -> {
				userServiceImpl.changePassword(userId, changePwReqDto);
			});
		}

		@Test
		@DisplayName("기존 비밀번호가 일치하지 않을 경우 예외를 던진다.")
		void notMatchOldPassword() {
			// Arrange
			String userId = "test";
			ChangePwReqDto changePwReqDto = ChangePwReqDto.builder()
				.currentPassword("old")
				.newPassword("new")
				.confirmPassword("new")
				.build();
			User user = User.builder()
				.password("notMatch")
				.build();
			when(userRepository.findById(eq(userId)))
				.thenReturn(Optional.of(user));
			// Act & Assert
			assertThrows(IllegalArgumentException.class, () -> {
				userServiceImpl.changePassword(userId, changePwReqDto);
			});
		}

		@Test
		@DisplayName("새로운 비밀번호와 확인 비밀번호가 일치하지 않을 경우 예외를 던진다.")
		void notMatchConfirmPassword() {
			// Arrange
			String userId = "test";
			ChangePwReqDto changePwReqDto = ChangePwReqDto.builder()
				.currentPassword("old")
				.newPassword("new")
				.confirmPassword("notMatch")
				.build();
			User user = User.builder()
				.password("old")
				.build();
			when(userRepository.findById(eq(userId)))
				.thenReturn(Optional.of(user));
			// Act & Assert
			assertThrows(IllegalArgumentException.class, () -> {
				userServiceImpl.changePassword(userId, changePwReqDto);
			});
		}

		@Test
		@DisplayName("비밀번호 변경에 실패할 경우 예외를 던진다.")
		void changePasswordFailed() {
			// Arrange
			String userId = "test";
			ChangePwReqDto changePwReqDto = ChangePwReqDto.builder()
				.currentPassword("old")
				.newPassword("new")
				.confirmPassword("new")
				.build();
			User user = User.builder()
				.password("old")
				.build();
			when(userRepository.findById(eq(userId)))
				.thenReturn(Optional.of(user));
			when(bCryptPasswordEncoder.matches(any(), any())).thenReturn(true);
			when(bCryptPasswordEncoder.encode(any())).thenThrow(new RuntimeException());
			// Act & Assert
			assertThrows(BusinessException.class, () -> {
				userServiceImpl.changePassword(userId, changePwReqDto);
			});
		}
	}

	@Nested
	@DisplayName("changeEmail")
	class ChangeEmail {

		@Test
		@DisplayName("이메일 변경에 성공한다.")
		void success() {
			//TODO : 이메일 변경 테스트 구현
			userServiceImpl.changeEmail("test", "newEmail");
		}
	}

	@Nested
	@DisplayName("emailVerification")
	class EmailVerification {

		@Test
		@DisplayName("이메일 인증에 성공한다.")
		void success() {
			//TODO : 이메일 인증 테스트 구현
			userServiceImpl.emailVerification(User.builder().build(), "code");
		}
	}

	@Nested
	@DisplayName("saveUser")
	class SaveUser {

		@Test
		@DisplayName("유저 저장에 성공한다.")
		void success() {
			// Arrange
			User user = User.builder().build();
			when(userRepository.save(any()))
				.thenReturn(user);
			// Act & Assert
			userServiceImpl.saveUser(user);
		}
	}
}
