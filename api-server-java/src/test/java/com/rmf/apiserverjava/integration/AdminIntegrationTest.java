package com.rmf.apiserverjava.integration;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmf.apiserverjava.config.annotation.IntegrationTest;
import com.rmf.apiserverjava.dto.users.UserResponseDto;
import com.rmf.apiserverjava.entity.users.Email;
import com.rmf.apiserverjava.entity.users.User;

import jakarta.persistence.EntityManager;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class AdminIntegrationTest {

	@Autowired
	EntityManager em;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Nested
	@DisplayName("GET /admin/users")
	class GetAllUsers {

		@Test
		@DisplayName("유저가 존재하지 않을 경우 빈 리스트를 반환한다.")
		public void noUser() throws Exception {
			// Arrange

			// Act
			String response = mockMvc.perform(get("/admin/users"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			//Assert
			assertThat(response).isEqualTo("[]");
		}

		@Test
		@DisplayName("유저가 존재할 경우 유저 목록을 반환한다.")
		public void existUser() throws Exception {
			// Arrange
			Email email = Email.builder().email("email").build();

			User user = User.builder().username("test")
				.password("password")
				.email(email)
				.isAdmin(false).build();
			em.persist(email);
			em.persist(user);
			em.flush();
			em.clear();

			// Act
			String response = mockMvc.perform(get("/admin/users"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			//Assert
			assertThat(response).contains("test");
		}

		@Test
		@DisplayName("username이 존재할 시 name으로 시작하는 유저 목록을 반환한다")
		public void existName() throws Exception {
			// Arrange
			for (int i = 0; i < 10; i++) {
				Email email = Email.builder().email("email" + i).build();

				User user = User.builder().username(i + "test")
					.password("password")
					.email(email)
					.isAdmin(false).build();
				em.persist(email);
				em.persist(user);
			}
			em.flush();
			em.clear();

			// Act
			String response = mockMvc.perform(get("/admin/users")
					.param("username", "3"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			//Assert
			System.out.println("response = " + response);
			assertThat(response).isEqualTo("[\"3test\"]");
		}

		@Test
		@DisplayName("isAdmin이 true일 경우 admin 유저 목록을 반환한다.")
		public void existIsAdmin() throws Exception {
			// Arrange
			for (int i = 0; i < 10; i++) {
				Email email = Email.builder().email("email" + i).build();

				User user = User.builder().username("test" + i)
					.password("password")
					.email(email)
					.isAdmin(i % 2 == 0).build();
				em.persist(email);
				em.persist(user);
			}
			em.flush();
			em.clear();

			// Act
			String response = mockMvc.perform(get("/admin/users")
					.param("is_admin", "true"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			//Assert
			System.out.println("response = " + response);
			assertThat(response.split(",").length).isEqualTo(5);
			assertThat(response).contains("test0", "test2", "test4", "test6", "test8");
		}

		@Test
		@DisplayName("isAdmin이 false일 경우 일반 유저 목록을 반환한다.")
		public void existIsNotAdmin() throws Exception {
			// Arrange
			for (int i = 0; i < 10; i++) {
				Email email = Email.builder().email("email" + i).build();

				User user = User.builder().username("test" + i)
					.password("password")
					.email(email)
					.isAdmin(i % 2 == 0).build();
				em.persist(email);
				em.persist(user);
			}
			em.flush();
			em.clear();

			// Act
			String response = mockMvc.perform(get("/admin/users")
					.param("is_admin", "false"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			//Assert
			System.out.println("response = " + response);
			assertThat(response.split(",").length).isEqualTo(5);
			assertThat(response).contains("test1", "test3", "test5", "test7", "test9");
		}

		@Test
		@DisplayName("limit와 offset을 이용하여 유저 목록을 반환한다.")
		public void limitAndOffset() throws Exception {
			// Arrange
			for (int i = 0; i < 10; i++) {
				Email email = Email.builder().email("email" + i).build();

				User user = User.builder().username("test" + i)
					.password("password")
					.email(email)
					.isAdmin(false).build();
				em.persist(email);
				em.persist(user);
			}
			em.flush();
			em.clear();

			// Act
			String response = mockMvc.perform(get("/admin/users")
					.param("limit", "5")
					.param("offset", "5"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			//Assert
			System.out.println("response = " + response);
			assertThat(response).contains("test5", "test6", "test7", "test8", "test9");
		}

		@Test
		@DisplayName("limit와 offset이 적절한 값이 아닐 경우 무시한다.")
		public void invalidLimitAndOffset() throws Exception {
			// Arrange
			for (int i = 0; i < 5; i++) {
				Email email = Email.builder().email("email" + i).build();

				User user = User.builder().username("test" + i)
					.password("password")
					.email(email)
					.isAdmin(false).build();
				em.persist(email);
				em.persist(user);
			}
			em.flush();
			em.clear();

			// Act
			String response = mockMvc.perform(get("/admin/users")
					.param("limit", "-1")
					.param("offset", "-1"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			//Assert
			System.out.println("response = " + response);
			assertThat(response).contains("test0", "test1", "test2", "test3", "test4");
		}

		@Test
		@DisplayName("order_by가 존재하고, -가 붙어있을 경우 해당하는 컬럼을 기준으로 내림차순으로 정렬하여 유저 목록을 반환한다.")
		public void orderByDesc() throws Exception {
			// Arrange
			for (int i = 0; i < 10; i++) {
				Email email = Email.builder().email("email" + i).build();

				User user = User.builder().username("test" + i)
					.password("password")
					.email(email)
					.isAdmin(false).build();
				em.persist(email);
				em.persist(user);
			}
			em.flush();
			em.clear();

			// Act
			String response = mockMvc.perform(get("/admin/users")
					.param("limit", "5")
					.param("offset", "3")
					.param("order_by", "-id,-email"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			//Assert
			System.out.println("response = " + response);
			assertThat(response).contains("test6", "test5", "test4", "test3", "test2");
		}

		@Test
		@DisplayName("order_by가 존재하고, -가 붙어있지 않을 경우 해당하는 컬럼을 기준으로 오름차순으로 정렬하여 유저 목록을 반환한다.")
		public void orderByAsc() throws Exception {
			// Arrange
			for (int i = 0; i < 10; i++) {
				Email email = Email.builder().email("email" + i).build();

				User user = User.builder().username("test" + i)
					.password("password")
					.email(email)
					.isAdmin(false).build();
				em.persist(email);
				em.persist(user);
			}
			em.flush();
			em.clear();

			// Act
			String response = mockMvc.perform(get("/admin/users")
					.param("limit", "5")
					.param("offset", "3")
					.param("order_by", "id,email"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			//Assert
			System.out.println("response = " + response);
			assertThat(response).contains("test3", "test4", "test5", "test6", "test7");
		}

		@Test
		@DisplayName("order_by가 존재하나, 해당하는 컬럼이 존재하지 않을 경우 무시한다.")
		public void invalidOrderBy() throws Exception {
			// Arrange
			for (int i = 0; i < 10; i++) {
				Email email = Email.builder().email("email" + i).build();

				User user = User.builder().username("test" + i)
					.password("password")
					.email(email)
					.isAdmin(false).build();
				em.persist(email);
				em.persist(user);
			}
			em.flush();
			em.clear();

			// Act
			String response = mockMvc.perform(get("/admin/users")
					.param("limit", "5")
					.param("offset", "3")
					.param("order_by", "invalid"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			//Assert
			System.out.println("response = " + response);
			assertThat(response).contains("test3", "test4", "test5", "test6", "test7");
		}
	}

	@Nested
	@DisplayName("GET /admin/users/{userid}")
	class GetUser {

		@Test
		@DisplayName("존재하지 않는 id로 요청하면 404 Not Found를 반환한다.")
		public void notExistId() throws Exception {
			// Act and Assert
			mockMvc.perform(get("/admin/users/test"))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("존재하는 id로 요청하면 유저 정보를 반환한다.")
		public void existId() throws Exception {
			// Arrange
			Email email = Email.builder().email("email").build();

			User user = User.builder().username("test")
				.password("password")
				.email(email)
				.isAdmin(false).build();
			em.persist(email);
			em.persist(user);
			em.flush();
			em.clear();

			// Act
			String response = mockMvc.perform(get("/admin/users/test"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			//Assert
			UserResponseDto userResponseDto = objectMapper.readValue(response, UserResponseDto.class);
			assertThat(userResponseDto.getUsername()).isEqualTo("test");
			assertThat(userResponseDto.getEmail()).isEqualTo("email");
		}
	}

	@Nested
	@DisplayName("POST /admin/users")
	class CreateUser {

		@Test
		@DisplayName("유저 생성에 성공하면 201 Created를 반환하고 유저 생성에 성공한다.")
		public void success() throws Exception {
			// Arrange

			// Act and Assert
			mockMvc.perform(post("/admin/users")
					.contentType("application/json")
					.content("{\"username\":\"test\", \"email\":\"email\" , \"is_admin\":true}"))
				.andExpect(status().isCreated());

			//Assert
			User user = em.find(User.class, "test");
			assertThat(user).isNotNull();
			assertThat(user.getUsername()).isEqualTo("test");
			assertThat(user.getEmail().getEmail()).isEqualTo("email");
			assertThat(user.getIsAdmin()).isTrue();
		}

		@Test
		@DisplayName("id가 중복될 경우 400 Bad Request를 반환하고 생성에 실패한다")
		public void duplicateId() throws Exception {
			// Arrange
			Email email = Email.builder().email("email").build();

			User user = User.builder().username("test")
				.password("password")
				.email(email)
				.isAdmin(false).build();
			em.persist(email);
			em.persist(user);
			em.flush();
			em.clear();

			// Act
			mockMvc.perform(post("/admin/users")
					.contentType("application/json")
					.content("{\"username\":\"test\", \"email\":\"email\" , \"isAdmin\":false}"))
				.andExpect(status().isBadRequest());

			//Assert
			User user1 = em.find(User.class, "test");
			assertThat(user1.getEmail().getEmail()).isEqualTo("email");
		}

		@Test
		@DisplayName("email이 중복될 경우 400 Bad Request를 반환하고 생성에 실패한다.")
		public void duplicateEmail() throws Exception {
			// Arrange
			Email email = Email.builder().email("email").build();

			User user = User.builder().username("test")
				.password("password")
				.email(email)
				.isAdmin(false).build();
			em.persist(email);
			em.persist(user);
			em.flush();
			em.clear();

			// Act
			mockMvc.perform(post("/admin/users")
					.contentType("application/json")
					.content("{\"username\":\"test\", \"email\":\"email\" , \"isAdmin\":false}"))
				.andExpect(status().isBadRequest());

			//Assert
			User user1 = em.find(User.class, "test1");
			assertThat(user1).isNull();
		}
	}

	@Nested
	@DisplayName("DELETE /admin/users/{userid}")
	class DeleteUser {

		@Test
		@DisplayName("존재하지 않는 id로 요청하면 404 Not Found를 반환한다.")
		public void notExistId() throws Exception {

			// Act and Assert
			mockMvc.perform(delete("/admin/users/test1"))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("존재하는 id로 요청하면 204 No Content를 반환하고, 유저를 삭제한다.")
		public void existId() throws Exception {
			// Arrange
			Email email = Email.builder().email("email").build();

			User user = User.builder().username("test")
				.password("password")
				.email(email)
				.isAdmin(false).build();
			em.persist(email);
			em.persist(user);
			em.flush();
			em.clear();

			// Act
			mockMvc.perform(delete("/admin/users/test"))
				.andExpect(status().isNoContent());

			//Assert
			User user1 = em.find(User.class, "test");
			assertThat(user1).isNull();
		}
	}

	@Nested
	@DisplayName("GET /admin/roles")
	class GetRoles {
		@Test
		@DisplayName("모든 권한을 반환한다.")
		public void getAllRoles() throws Exception {
			// Act
			String response = mockMvc.perform(get("/admin/roles"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			//Assert
			//현재는 빈 리스트를 반환중이다.
			//TODO : 권한이 추가되면 통합 테스트도 추가한다.
		}
	}
}
