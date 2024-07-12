package com.rmf.apiserverjava.integration;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

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
import com.rmf.apiserverjava.dto.alerts.AlertResponseDto;
import com.rmf.apiserverjava.entity.alerts.Alert;
import com.rmf.apiserverjava.entity.tasks.TaskEventLog;
import com.rmf.apiserverjava.entity.tasks.TaskState;
import com.rmf.apiserverjava.entity.users.Email;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.utils.JwtUtil;
import com.rmf.apiserverjava.repository.TaskEventLogRepository;
import com.rmf.apiserverjava.rmfapi.tasks.TaskStateApi;
import com.rmf.apiserverjava.security.UserSession;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;

@IntegrationTestWithSecurity
@AutoConfigureMockMvc
@Transactional
public class AlertIntegrationTest {
	@Autowired
	EntityManager em;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TaskEventLogRepository taskEventLogRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserSession userSession;

	Cookie access;
	String tokenUsername;

	@BeforeEach
	void setUp() {
		tokenUsername = "tokenUser";
		Email email = Email.builder().email("email").isVerified(true).build();
		User user = new User(tokenUsername, "password", false, email);
		em.persist(email);
		em.persist(user);
		em.flush();
		userSession.renewSession(tokenUsername);
		access = jwtUtil.createJwtCookie(tokenUsername, "ROLE_USER", JwtUtil.TokenType.ACCESS);
	}

	@Nested
	@DisplayName("GET /alerts")
	class GetAlerts {
		@Test
		@DisplayName("DB에 alert가 존재하지 않을 경우 빈 리스트를 반환한다.")
		void notExist() throws Exception {
			//Arrange
			//Act
			String res = mockMvc.perform(get("/alerts").cookie(access))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			List<AlertResponseDto> response = objectMapper.readValue(res,
				objectMapper.getTypeFactory().constructCollectionType(List.class, AlertResponseDto.class));

			//Assert
			assertThat(response).isNotNull();
			assertThat(response.size()).isEqualTo(0);
		}

		@Test
		@DisplayName("DB에 alert가 존재할 경우 전체 alert를 반환한다")
		void exist() throws Exception {
			//Arrange
			int size = 10;
			for (int i = 0; i < size; i++) {
				String id = String.valueOf(i);
				Alert alert = new Alert(id, id, Alert.Category.FLEET, System.currentTimeMillis());
				em.persist(alert);
			}
			em.flush();
			em.clear();

			//Act
			String res = mockMvc.perform(get("/alerts").cookie(access))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			List<AlertResponseDto> response = objectMapper.readValue(res,
				objectMapper.getTypeFactory().constructCollectionType(List.class, AlertResponseDto.class));

			//Assert
			assertThat(response).isNotNull();
			assertThat(response.size()).isEqualTo(size);
		}
	}

	@Nested
	@DisplayName("GET /alerts/{alertId}")
	class GetAlert {
		@Test
		@DisplayName("존재하지 않는 alertId로 요청하면 404 NOT_FOUND를 반환한다.")
		void notExist() throws Exception {
			//Arrange
			//Act
			//Assert
			mockMvc.perform(get("/alerts/" + "notExistId").cookie(access)).andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("존재하는 alertId로 요청하면 해당 Alert를 반환한다.")
		void exist() throws Exception {
			//Arrange
			Alert alert = new Alert("11", "11", Alert.Category.FLEET, System.currentTimeMillis());
			em.persist(alert);
			em.flush();
			em.clear();

			//Act
			String res = mockMvc.perform(get("/alerts/" + alert.getId()).cookie(access))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			AlertResponseDto response = objectMapper.readValue(res, AlertResponseDto.class);

			//Assert
			assertThat(response).isNotNull();
			assertThat(response.getId()).isEqualTo(alert.getId());
			assertThat(response.getOriginalId()).isEqualTo(alert.getOriginalId());
			assertThat(response.getAcknowledgedBy()).isEqualTo(alert.getAcknowledgedBy());
			assertThat(response.getUnixMillisCreatedTime()).isEqualTo(alert.getUnixMillisCreatedTime());
			assertThat(response.getCategory()).isEqualTo(alert.getCategory());
		}
	}

	@Nested
	@DisplayName("POST /alerts")
	class CreateAlert {
		@Test
		@DisplayName("기존에 존재하지 않는 alertId로 요청하면 새로운 Alert를 생성한다. Id, Category, CreatedTime이 설정되어야 한다.")
		void notExist() throws Exception {
			//Arrange
			String queryFormat = "alert_id=%s&category=%s";
			String notExistId = "notExistId";
			String validCategory = "fleet";
			String query = String.format(queryFormat, notExistId, validCategory);

			//Act
			String res = mockMvc.perform(post("/alerts?" + query).cookie(access))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			AlertResponseDto response = objectMapper.readValue(res, AlertResponseDto.class);

			//Assert
			assertThat(response).isNotNull();
			assertThat(response.getId()).isEqualTo(notExistId);
			assertThat(response.getOriginalId()).isEqualTo(notExistId);
			assertThat(response.getCategory()).isEqualTo(Alert.Category.fromValue(validCategory).get());
			assertThat(response.getUnixMillisCreatedTime()).isNotNull();
			assertThat(response.getAcknowledgedBy()).isNull();
			assertThat(response.getUnixMillisAcknowledgedTime()).isNull();
		}

		@Test
		@DisplayName("존재하는 alertId로 요청하면 해당 Alert 업데이트한다.")
		void exist() throws Exception {
			//Arrange
			String queryFormat = "alert_id=%s&category=%s";
			String existId = "existId";
			String previousCategoryValue = "fleet";
			String updateCategoryValue = "default";
			String query = String.format(queryFormat, existId, updateCategoryValue);
			Alert.Category previousCategory = Alert.Category.fromValue(previousCategoryValue).get();
			Alert.Category updateCategory = Alert.Category.fromValue(updateCategoryValue).get();

			Alert alert = new Alert(existId, existId, previousCategory, System.currentTimeMillis());
			em.persist(alert);
			em.flush();
			em.clear();

			//Act
			String res = mockMvc.perform(post("/alerts?" + query).cookie(access))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			AlertResponseDto response = objectMapper.readValue(res, AlertResponseDto.class);

			//Assert
			assertThat(response).isNotNull();
			assertThat(response.getId()).isEqualTo(alert.getId());
			assertThat(response.getOriginalId()).isEqualTo(alert.getOriginalId());
			assertThat(response.getCategory()).isEqualTo(updateCategory);
			assertThat(response.getUnixMillisCreatedTime()).isNotNull();
			assertThat(response.getAcknowledgedBy()).isNull();
			assertThat(response.getUnixMillisAcknowledgedTime()).isNull();
		}

		@Test
		@DisplayName("유효하지 않은 category value로 요청하면 400에러를 반환한다")
		void invalidCategory() throws Exception {
			//Arrange
			String queryFormat = "alert_id=%s&category=%s";
			String notExistId = "notExistId";
			String invalidCategory = "notExistCategory";
			String query = String.format(queryFormat, notExistId, invalidCategory);

			//Act
			//Assert
			mockMvc.perform(post("/alerts?" + query).cookie(access)).andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("category를 null로 요청하면 400에러를 반환한다")
		void invalidNullCategory() throws Exception {
			//Arrange
			String queryFormat = "alert_id=%s";
			String notExistId = "notExistId";
			String query = String.format(queryFormat, notExistId);

			//Act
			//Assert
			mockMvc.perform(post("/alerts?" + query).cookie(access)).andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("alertId를 null로 요청하면 400에러를 반환한다")
		void invalidNullAlertId() throws Exception {
			//Arrange
			String queryFormat = "category=%s";
			String category = Alert.Category.DEFAULT.getValue();
			String query = String.format(queryFormat, category);

			//Act
			//Assert
			mockMvc.perform(post("/alerts?" + query).cookie(access)).andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("alertId를 빈 상태로 요청하면 400에러를 반환한다")
		void invalidEmptyAlertId() throws Exception {
			//Arrange
			String queryFormat = "alert_id=%s&category=%s";
			String category = Alert.Category.DEFAULT.getValue();
			String alertId = "";
			String query = String.format(queryFormat, alertId, category);

			//Act
			//Assert
			mockMvc.perform(post("/alerts?" + query).cookie(access)).andExpect(status().isBadRequest());
		}
	}

	@Nested
	@DisplayName("POST /alerts/{alertId}")
	class AcknowledgeAlert {
		@Test
		@DisplayName("존재하지 않는 alertId로 요청할 경우 404 에러를 반환한다")
		void notExistID() throws Exception {

			//Arrange
			String notExistId = "notExistId";

			//Act
			//Assert
			mockMvc.perform(post("/alerts/" + notExistId).cookie(access)).andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("이미 인지된 알림일 경우 기존 알림을 전달한다.")
		void alreadyAcknowledge() throws Exception {
			//Arrange
			String existUsername = "username";
			String existId = "existId";
			Alert.Category category = Alert.Category.FLEET;
			long unixMillis = System.currentTimeMillis();

			Alert alert = new Alert(existId, existId, category, unixMillis);
			alert.acknowledge(unixMillis, existUsername);
			em.persist(alert);
			em.flush();
			em.clear();

			//Act
			String res = mockMvc.perform(post("/alerts/" + existId).cookie(access))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			AlertResponseDto response = objectMapper.readValue(res, AlertResponseDto.class);

			//Assert
			assertThat(response.getId()).isEqualTo(existId + Alert.ACK_ID_PREFIX + unixMillis);
			assertThat(response.getOriginalId()).isEqualTo(existId);
			assertThat(response.getCategory()).isEqualTo(category);
			assertThat(response.getUnixMillisCreatedTime()).isEqualTo(unixMillis);
			assertThat(response.getUnixMillisAcknowledgedTime()).isEqualTo(unixMillis);
			assertThat(response.getAcknowledgedBy()).isEqualTo(existUsername);
		}

		@Test
		@DisplayName("ID가 일치하는 알림 및 TaskState, TaskEventLog가 존재하고 인지되지 않았을 경우 acknowledge 이벤트를 수행한다.")
		void acknowledgeEventSuccess() throws Exception {
			//Arrange
			String existId = "existId";
			Alert.Category category = Alert.Category.FLEET;
			long unixMillis = System.currentTimeMillis();

			Alert alert = new Alert(existId, existId, category, unixMillis);
			TaskEventLog taskEventLog = new TaskEventLog(existId);
			TaskState taskState = TaskState.builder().id(existId).data(TaskStateApi.builder().build()).build();
			em.persist(alert);
			em.persist(taskEventLog);
			em.persist(taskState);
			em.flush();
			em.clear();

			//Act
			String res = mockMvc.perform(post("/alerts/" + existId).cookie(access))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			AlertResponseDto response = objectMapper.readValue(res, AlertResponseDto.class);
			em.flush();
			em.clear();
			TaskEventLog taskEventLogResult = taskEventLogRepository.findById(existId).get();
			TaskState taskStateResult = em.find(TaskState.class, existId);

			//Assert
			assertThat(response.getId()).contains(existId + Alert.ACK_ID_PREFIX);
			assertThat(response.getOriginalId()).isEqualTo(existId);
			assertThat(response.getCategory()).isEqualTo(category);
			assertThat(response.getUnixMillisCreatedTime()).isEqualTo(unixMillis);
			assertThat(response.getAcknowledgedBy()).isEqualTo(tokenUsername);
			assertThat(taskStateResult.getData().getPhases().size()).isEqualTo(1);
			assertThat(taskEventLogResult.getPhases().size()).isEqualTo(1);
		}

		@Test
		@DisplayName("ID가 일치하는 TaskState가 존재하고 TaskLog가 존재하지 않을 경우 TaskState만 변경된다.")
		void taskStateNotExist() throws Exception {
			//Arrange
			String existId = "existId";
			Alert.Category category = Alert.Category.FLEET;
			long unixMillis = System.currentTimeMillis();

			Alert alert = new Alert(existId, existId, category, unixMillis);
			TaskState taskState = TaskState.builder().id(existId).data(TaskStateApi.builder().build()).build();
			em.persist(alert);
			em.persist(taskState);
			em.flush();
			// em.clear();

			//Act
			mockMvc.perform(post("/alerts/" + existId).cookie(access)).andExpect(status().isOk());

			//Assert
			assertThat(taskState.getData().getPhases().size()).isEqualTo(1);
		}

		@Test
		@DisplayName("ID가 일치하는 TaskLog가 존재하고 TaskState가 존재하지 않을 경우 TaskLog만 변경된다.")
		void taskEventLogNotExist() throws Exception {
			//Arrange
			String existId = "existId";
			Alert.Category category = Alert.Category.FLEET;
			long unixMillis = System.currentTimeMillis();

			Alert alert = new Alert(existId, existId, category, unixMillis);
			TaskEventLog taskEventLog = new TaskEventLog(existId);
			em.persist(alert);
			em.persist(taskEventLog);
			em.flush();
			em.clear();

			//Act
			mockMvc.perform(post("/alerts/" + existId).cookie(access)).andExpect(status().isOk());
			em.flush();
			em.clear();

			//Assert
			taskEventLog = taskEventLogRepository.findById(existId).get();
			assertThat(taskEventLog.getPhases().size()).isEqualTo(1);
		}
	}
}
