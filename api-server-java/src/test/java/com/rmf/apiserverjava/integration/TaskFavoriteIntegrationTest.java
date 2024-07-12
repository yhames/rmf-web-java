package com.rmf.apiserverjava.integration;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmf.apiserverjava.config.annotation.IntegrationTestWithSecurity;
import com.rmf.apiserverjava.dto.tasks.TaskFavoriteDto;
import com.rmf.apiserverjava.entity.tasks.TaskFavorite;
import com.rmf.apiserverjava.entity.users.Email;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.utils.JwtUtil;
import com.rmf.apiserverjava.repository.TaskFavoriteRepository;
import com.rmf.apiserverjava.security.UserSession;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;

@IntegrationTestWithSecurity
@AutoConfigureMockMvc
@Transactional
public class TaskFavoriteIntegrationTest {

	@Autowired
	EntityManager em;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private TaskFavoriteRepository taskFavoriteRepository;

	@Autowired
	private UserSession userSession;

	Cookie access;
	String username;

	@BeforeEach
	void setUp() {
		username = "tokenUser";
		Email email = Email.builder().email("email").isVerified(true).build();
		User user = new User(username, "password", false, email);
		em.persist(email);
		em.persist(user);
		em.flush();
		userSession.renewSession(username);
		access = jwtUtil.createJwtCookie(username, "ROLE_USER", JwtUtil.TokenType.ACCESS);
	}

	@Nested
	@DisplayName("GET /favorite_task")
	class GetFavoriteTasks {
		@Test
		@DisplayName("DB에 TaskFavoriteDto가 존재하지 않을 경우 빈 리스트를 반환한다.")
		void notExist() throws Exception {
			//Arrange
			//Act
			String res = mockMvc.perform(get("/favorite_tasks").cookie(access))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			List<TaskFavoriteDto> response = objectMapper.readValue(res,
				objectMapper.getTypeFactory().constructCollectionType(List.class, TaskFavoriteDto.class));

			//Assert
			assertThat(response).isNotNull();
			assertThat(response.size()).isEqualTo(0);
		}

		@Test
		@DisplayName("DB에 username과 일치하는 TaskFavoriteDto 데이터들을 전체 반환한다")
		void exist() throws Exception {
			//Arrange
			int size = 10;
			for (int i = 0; i < size; i++) {
				String id = String.valueOf(i);
				TaskFavorite taskFavorite = TaskFavorite
					.builder()
					.user(username)
					.name("taskName")
					.category("category")
					.description(Map.of())
					.priority(Map.of())
					.build();
				em.persist(taskFavorite);
			}
			String differentUser = "differentUser";
			for (int i = 0; i < size; i++) {
				String id = String.valueOf(i);
				TaskFavorite taskFavorite = TaskFavorite.builder()
					.user(differentUser)
					.name("taskName")
					.category("category")
					.description(Map.of())
					.priority(Map.of())
					.build();
				em.persist(taskFavorite);
			}
			em.flush();
			em.clear();

			//Act
			String res = mockMvc.perform(get("/favorite_tasks").cookie(access))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			List<TaskFavoriteDto> response = objectMapper.readValue(res,
				objectMapper.getTypeFactory().constructCollectionType(List.class, TaskFavoriteDto.class));

			//Assert
			assertThat(response).isNotNull();
			assertThat(response.size()).isEqualTo(size);
		}
	}

	@Nested
	@DisplayName("POST /favorite_task")
	class CreateFavoriteTask {
		@Test
		@DisplayName("새로운 Favorite Task를 생성한다.")
		void createSuccess() throws Exception {
			//Arrange
			TaskFavoriteDto taskFavoriteDto = TaskFavoriteDto.builder()
				.name("taskName")
				.category("category")
				.build();
			String taskFavoriteDtoJson = objectMapper.writeValueAsString(taskFavoriteDto);

			//Act
			String res = mockMvc.perform(post("/favorite_tasks?")
					.contentType("application/json").content(taskFavoriteDtoJson).cookie(access))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			TaskFavoriteDto response = objectMapper.readValue(res, TaskFavoriteDto.class);

			//Assert
			assertThat(response).isNotNull();
			assertThat(response.getUser()).isEqualTo(username);
			assertThat(response.getCategory()).isEqualTo(taskFavoriteDto.getCategory());
		}

		@Test
		@DisplayName("Favorite Task 생성 시 필수값이 누락되면 400 BAD_REQUEST를 반환한다.")
		void createFailByBadRequest() throws Exception {
			//Arrange
			TaskFavoriteDto taskFavoriteDto = TaskFavoriteDto.builder()
				.category("category")
				.build();
			String taskFavoriteDtoJson = objectMapper.writeValueAsString(taskFavoriteDto);

			//Act
			//Assert
			mockMvc.perform(post("/favorite_tasks?")
					.contentType("application/json").content(taskFavoriteDtoJson).cookie(access))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("Favorite Task 생성 시 id값이 null일 경우 새로 생성하여 저장한다")
		void createNewTaskFavorite() throws Exception {
			//Arrange
			TaskFavoriteDto taskFavoriteDto = TaskFavoriteDto.builder()
				.name("taskName")
				.category("category")
				.build();
			String taskFavoriteDtoJson = objectMapper.writeValueAsString(taskFavoriteDto);

			//Act
			String res = mockMvc.perform(post("/favorite_tasks?")
					.contentType("application/json").content(taskFavoriteDtoJson).cookie(access))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			TaskFavoriteDto response = objectMapper.readValue(res, TaskFavoriteDto.class);

			//Assert
			assertThat(response).isNotNull();
			assertThat(response.getUser()).isEqualTo(username);
			assertThat(response.getCategory()).isEqualTo(taskFavoriteDto.getCategory());
			assertThat(taskFavoriteRepository.findAll().size()).isEqualTo(1);
		}

		@Test
		@DisplayName("Favorite Task 생성 시 id값이 empty일 경우 새로 생성하여 저장한다")
		void createNewTaskFavoriteWhenEmptyId() throws Exception {
			//Arrange
			TaskFavoriteDto taskFavoriteDto = TaskFavoriteDto.builder()
				.id("")
				.name("taskName")
				.category("category")
				.build();
			String taskFavoriteDtoJson = objectMapper.writeValueAsString(taskFavoriteDto);

			//Act
			String res = mockMvc.perform(post("/favorite_tasks?")
					.contentType("application/json").content(taskFavoriteDtoJson).cookie(access))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			TaskFavoriteDto response = objectMapper.readValue(res, TaskFavoriteDto.class);

			//Assert
			assertThat(response).isNotNull();
			assertThat(response.getUser()).isEqualTo(username);
			assertThat(response.getCategory()).isEqualTo(taskFavoriteDto.getCategory());
			assertThat(taskFavoriteRepository.findAll().size()).isEqualTo(1);
		}

		@Test
		@DisplayName("Favorite Task 생성 시 id값이 존재하고 동일한 유저일 경우 업데이트한다.")
		void updateTaskFavorite() throws Exception {
			//Arrange
			TaskFavorite taskFavorite = TaskFavorite
				.builder()
				.user(username)
				.name("taskName")
				.category("category")
				.description(Map.of())
				.priority(Map.of())
				.build();
			em.persist(taskFavorite);
			em.flush();
			String existId = taskFavorite.getId();
			em.clear();
			TaskFavoriteDto taskFavoriteDto = TaskFavoriteDto.builder()
				.id(existId)
				.name("updatedTaskName")
				.unixMillisEarliestStartTime(1000L)
				.category("updatedCategory")
				.build();
			String taskFavoriteDtoJson = objectMapper.writeValueAsString(taskFavoriteDto);

			//Act
			String res = mockMvc.perform(post("/favorite_tasks?")
					.contentType("application/json").content(taskFavoriteDtoJson).cookie(access))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			TaskFavoriteDto response = objectMapper.readValue(res, TaskFavoriteDto.class);

			//Assert
			assertThat(response).isNotNull();
			assertThat(response.getUser()).isEqualTo(username);
			assertThat(response.getCategory()).isEqualTo(taskFavoriteDto.getCategory());
			assertThat(response.getName()).isEqualTo(taskFavoriteDto.getName());
			assertThat(taskFavoriteRepository.findAll().size()).isEqualTo(1);
		}

		@Test
		@DisplayName("Favorite Task 생성 시 id값이 존재하지만 user가 동일하지 않은 사용자가 요청할 경우 403 에러가 발생한다.")
		void updateTaskFavoriteFailedDifferentUser() throws Exception {
			//Arrange
			TaskFavorite taskFavorite = TaskFavorite
				.builder()
				.user(username + "not same")
				.name("taskName")
				.category("category")
				.description(Map.of())
				.priority(Map.of())
				.build();
			em.persist(taskFavorite);
			em.flush();
			String existId = taskFavorite.getId();
			em.clear();
			TaskFavoriteDto taskFavoriteDto = TaskFavoriteDto.builder()
				.id(existId)
				.name("updatedTaskName")
				.unixMillisEarliestStartTime(1000L)
				.category("updatedCategory")
				.build();
			String taskFavoriteDtoJson = objectMapper.writeValueAsString(taskFavoriteDto);

			//Act
			//Assert
			mockMvc.perform(post("/favorite_tasks?")
					.contentType("application/json").content(taskFavoriteDtoJson).cookie(access))
				.andExpect(status().isForbidden());

		}

		@Test
		@DisplayName("Favorite Task 생성 시 id값이 전달했지만 해당하는 Id의 Favorite Task가 존재하지 않을경우 404 예외를 반환한다.")
		void updateTaskUpdateFavoriteFailedNotExist() throws Exception {
			//Arrange
			TaskFavorite taskFavorite = TaskFavorite
				.builder()
				.user(username)
				.name("taskName")
				.category("category")
				.description(Map.of())
				.priority(Map.of())
				.build();
			em.persist(taskFavorite);
			em.flush();
			String differentId = taskFavorite.getId() + "different";
			em.clear();
			TaskFavoriteDto taskFavoriteDto = TaskFavoriteDto.builder()
				.id(differentId)
				.name("updatedTaskName")
				.unixMillisEarliestStartTime(1000L)
				.category("updatedCategory")
				.build();
			String taskFavoriteDtoJson = objectMapper.writeValueAsString(taskFavoriteDto);

			//Act
			//Assert
			mockMvc.perform(post("/favorite_tasks?")
					.contentType("application/json").content(taskFavoriteDtoJson).cookie(access))
				.andExpect(status().isNotFound());

		}
	}

	@Nested
	@DisplayName("DELETE /favorite_task/{favoriteTaskId}")
	class DeleteFavoriteTask {
		@Test
		@DisplayName("존재하지 않는 TaskFavorite를 삭제 시도할 경우 404 NOT_FOUND를 반환한다.")
		void isNotExistDeleteFailedSuccess() throws Exception {
			//Arrange
			String notExistTaskId = UUID.randomUUID().toString();

			//Act
			//Assert
			mockMvc.perform(delete("/favorite_task/" + notExistTaskId).cookie(access))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("존재하는 본인의 TaskFavorite는 ID를 통해 삭제할 수 있다.")
		void deleteSuccess() throws Exception {
			//Arrange
			TaskFavorite taskFavorite = TaskFavorite
				.builder()
				.user(username)
				.name("taskName")
				.category("category")
				.description(Map.of())
				.priority(Map.of())
				.build();
			em.persist(taskFavorite);
			em.flush();
			String existId = taskFavorite.getId();
			em.clear();

			//Act
			//Assert
			mockMvc.perform(delete("/favorite_tasks/" + existId).cookie(access))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("존재하지 않는 TaskFavorite의 삭제시도는 404 예외를 반환한다.")
		void deleteSuccessFailedByNotExist() throws Exception {
			//Arrange
			String notExistId = "notExsist";
			em.clear();

			//Act
			//Assert
			mockMvc.perform(delete("/favorite_tasks/" + notExistId).cookie(access))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("존재하지만 본인의 FavoriteTask가 아닌경우 접근이 거부된다.")
		void deleteFailByForbidden() throws Exception {
			//Arrange
			String taskUsername = "taskUsername";
			TaskFavorite taskFavorite = TaskFavorite
				.builder()
				.user(taskUsername)
				.name("taskName")
				.category("category")
				.description(Map.of())
				.priority(Map.of())
				.build();
			em.persist(taskFavorite);
			em.flush();
			String existId = taskFavorite.getId();
			em.clear();

			//Act
			//Assert
			mockMvc.perform(delete("/favorite_tasks/" + existId).cookie(access))
				.andExpect(status().isForbidden()).andReturn().getResponse().getContentAsString();
		}
	}
}
