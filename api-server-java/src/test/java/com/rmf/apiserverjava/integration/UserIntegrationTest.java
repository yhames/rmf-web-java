package com.rmf.apiserverjava.integration;

import static com.rmf.apiserverjava.service.impl.UserServiceImpl.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmf.apiserverjava.config.annotation.IntegrationTestWithSecurity;
import com.rmf.apiserverjava.dto.users.ChangePwReqDto;
import com.rmf.apiserverjava.dto.users.UserResponseDto;
import com.rmf.apiserverjava.entity.users.Email;
import com.rmf.apiserverjava.entity.users.User;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;

@IntegrationTestWithSecurity
@AutoConfigureMockMvc
@Transactional
public class UserIntegrationTest {

	@Autowired
	EntityManager em;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	private static final String TEST_USERNAME = "user1";
	private static final String TEST_PASSWORD = "1234";

	@BeforeEach
	public void setUp() {
		//Arrange
		// 로그인 요청을 위한 사용자 정보를 생성하여 저장합니다.
		Email email = Email.builder().email("email").isVerified(true).build();

		User user = com.rmf.apiserverjava.entity.users.User.builder()
			.username(TEST_USERNAME)
			.email(email)
			.isAdmin(false)
			.password(bCryptPasswordEncoder.encode(TEST_PASSWORD))
			.build();

		em.persist(email);
		em.persist(user);
		em.flush();
		em.clear();
	}

	@Nested
	@DisplayName("GET /user")
	class GetMyInfo {

		@Test
		@DisplayName("내 정보를 조회한다.")
		public void getMyInfo() throws Exception {
			//Arrange
			// 로그인 요청을 시뮬레이션하고 응답에서 JWT 토큰을 추출합니다.
			List<String> cookieHeaders = mockMvc.perform(post("/login")
					.contentType("application/x-www-form-urlencoded")
					.content("username=" + TEST_USERNAME + "&password=" + TEST_PASSWORD))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse().getHeaders("Set-Cookie");

			List<Cookie> cookies = cookieHeaders.stream().map(header -> {
				String[] cookieParts = header.split(";");
				String[] nameAndValue = cookieParts[0].split("=");
				return nameAndValue.length < 2 ? null : new Cookie(nameAndValue[0], nameAndValue[1]);
			})
				.filter(cookie -> cookie != null)
				.collect(Collectors.toList());

			// Act
			String content = mockMvc.perform(get("/user")
					.cookie(cookies.toArray(new Cookie[0])))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

			// Assert
			UserResponseDto response = objectMapper.readValue(content, UserResponseDto.class);
			assertThat(response.getUsername()).isEqualTo(TEST_USERNAME);
		}
	}

	@Nested
	@DisplayName("POST /user/password")
	class ChangeMyPassword {

		@Test
		@DisplayName("내 비밀번호를 변경한다.")
		public void changeMyPassword() throws Exception {
			//Arrange
			// 로그인 요청을 시뮬레이션하고 응답에서 JWT 토큰을 추출합니다.
			List<String> cookieHeaders = mockMvc.perform(post("/login")
					.contentType("application/x-www-form-urlencoded")
					.content("username=" + TEST_USERNAME + "&password=" + TEST_PASSWORD))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse().getHeaders("Set-Cookie");

			List<Cookie> cookies = cookieHeaders.stream().map(header -> {
				String[] cookieParts = header.split(";");
				String[] nameAndValue = cookieParts[0].split("=");
				return nameAndValue.length < 2 ? null : new Cookie(nameAndValue[0], nameAndValue[1]);
			})
				.filter(cookie -> cookie != null)
				.collect(Collectors.toList());

			// Act
			String newPassword = "newPassword";
			String confirmPassword = "newPassword";
			mockMvc.perform(post("/user/password")
					.cookie(cookies.toArray(new Cookie[0]))
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(
						ChangePwReqDto.builder()
							.currentPassword(TEST_PASSWORD)
							.newPassword(newPassword)
							.confirmPassword(confirmPassword).build())
					))
				.andExpect(status().isNoContent());

			// Assert
			User changedUser = em.find(User.class, TEST_USERNAME);
			assertThat(bCryptPasswordEncoder.matches(newPassword, changedUser.getPassword())).isTrue();
		}

		@Test
		@DisplayName("내 비밀번호를 변경할 때 기존 비밀번호가 틀리면 예외를 반환한다.")
		public void changeMyPasswordWithWrongOldPassword() throws Exception {
			//Arrange
			// 로그인 요청을 시뮬레이션하고 응답에서 JWT 토큰을 추출합니다.
			List<String> cookieHeaders = mockMvc.perform(post("/login")
					.contentType("application/x-www-form-urlencoded")
					.content("username=" + TEST_USERNAME + "&password=" + TEST_PASSWORD))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse().getHeaders("Set-Cookie");

			List<Cookie> cookies = cookieHeaders.stream().map(header -> {
				String[] cookieParts = header.split(";");
				String[] nameAndValue = cookieParts[0].split("=");
				return nameAndValue.length < 2 ? null : new Cookie(nameAndValue[0], nameAndValue[1]);
			})
				.filter(cookie -> cookie != null)
				.collect(Collectors.toList());

			// Act
			String newPassword = "newPassword";
			String confirmPassword = "newPassword";
			MockHttpServletResponse response = mockMvc.perform(post("/user/password")
					.cookie(cookies.toArray(new Cookie[0]))
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(
						ChangePwReqDto.builder()
							.currentPassword("wrongPassword")
							.newPassword(newPassword)
							.confirmPassword(confirmPassword).build())
					))
				.andExpect(status().isBadRequest())
				.andReturn().getResponse();

			// Assert
			assertThat(response.getContentAsString()).contains(OLD_PASSWORD_ERROR_MSG);
		}

		@Test
		@DisplayName("내 비밀번호를 변경할 때 새 비밀번호와 확인 비밀번호가 다르면 예외를 반환한다.")
		public void changeMyPasswordWithDifferentNewPasswordAndConfirmPassword() throws Exception {
			//Arrange
			// 로그인 요청을 시뮬레이션하고 응답에서 JWT 토큰을 추출합니다.
			List<String> cookieHeaders = mockMvc.perform(post("/login")
					.contentType("application/x-www-form-urlencoded")
					.content("username=" + TEST_USERNAME + "&password=" + TEST_PASSWORD))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse().getHeaders("Set-Cookie");

			List<Cookie> cookies = cookieHeaders.stream().map(header -> {
				String[] cookieParts = header.split(";");
				String[] nameAndValue = cookieParts[0].split("=");
				return nameAndValue.length < 2 ? null : new Cookie(nameAndValue[0], nameAndValue[1]);
			})
				.filter(cookie -> cookie != null)
				.collect(Collectors.toList());

			// Act
			String newPassword = "newPassword";
			String confirmPassword = "differentPassword";
			MockHttpServletResponse response = mockMvc.perform(post("/user/password")
					.cookie(cookies.toArray(new Cookie[0]))
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(
						ChangePwReqDto.builder()
							.currentPassword(TEST_PASSWORD)
							.newPassword(newPassword)
							.confirmPassword(confirmPassword).build())
					))
				.andExpect(status().isBadRequest())
				.andReturn().getResponse();

			// Assert
			assertThat(response.getContentAsString()).contains(CONFIRM_PASSWORD_ERROR_MSG);
		}
	}
}
