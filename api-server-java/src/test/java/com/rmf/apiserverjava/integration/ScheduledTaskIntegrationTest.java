package com.rmf.apiserverjava.integration;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.rmf.apiserverjava.config.annotation.IntegrationTestWithSecurity;
import com.rmf.apiserverjava.dto.scheduledtasks.CreateScheduledTaskDto;
import com.rmf.apiserverjava.dto.scheduledtasks.CreateScheduledTaskRequestDto;
import com.rmf.apiserverjava.dto.scheduledtasks.ScheduledTaskResponseDto;
import com.rmf.apiserverjava.dto.scheduledtasks.ScheduledTaskScheduleRequestDto;
import com.rmf.apiserverjava.dto.scheduledtasks.UpdateScheduledTaskRequestDto;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTask;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTaskSchedule;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTaskSchedule.Period;
import com.rmf.apiserverjava.entity.users.Email;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.utils.ObjectMapperUtils;
import com.rmf.apiserverjava.jobs.ScheduledTaskJob;
import com.rmf.apiserverjava.rmfapi.tasks.TaskRequestApi;
import com.rmf.apiserverjava.service.QuartzService;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;

@IntegrationTestWithSecurity
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class ScheduledTaskIntegrationTest {
	@Autowired
	EntityManager em;

	@Autowired
	SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	QuartzService quartzService;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private MockMvc mockMvc;

	private List<Cookie> cookies;

	private static final String TEST_USERNAME = "user1";

	private static final String TEST_PASSWORD = "1234";

	@BeforeAll
	void setUp() throws Exception {
		schedulerFactoryBean.getScheduler().start();
	}

	@BeforeEach
	void setUpEach() throws Exception {
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

		List<String> cookieHeaders = mockMvc.perform(post("/login")
				.contentType("application/x-www-form-urlencoded")
				.content("username=" + TEST_USERNAME + "&password=" + TEST_PASSWORD))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse().getHeaders("Set-Cookie");
		cookies = cookieHeaders.stream().map(header -> {
			String[] cookieParts = header.split(";");
			String[] nameAndValue = cookieParts[0].split("=");
			return nameAndValue.length < 2 ? null : new Cookie(nameAndValue[0], nameAndValue[1]);
		}).filter(Objects::nonNull).toList();
	}

	@AfterAll
	void tearDown() throws SchedulerException {
		schedulerFactoryBean.getScheduler().shutdown();
	}

	@Nested
	@DisplayName("GET /scheduled_tasks")
	class GetScheduledTasks {
		@Test
		@DisplayName("조회에 성공할 경우 List<ScheduledTaskResponseDto>를 반환한다.")
		void exist() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.from(Instant.parse("2024-04-26T14:59:59.999Z"));
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.startFrom(timestamp)
				.until(timestamp)
				.build();
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			List<ScheduledTask> scheduledTasks = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				ScheduledTask scheduledTask = ScheduledTask.builder()
					.taskRequest(taskRequestApi)
					.createdBy("user")
					.schedules(List.of(scheduledTaskSchedule))
					.build();
				scheduledTasks.add(scheduledTask);
				em.persist(scheduledTask);
			}
			em.flush();
			em.clear();

			// Act
			String startBefore = "2024-04-28T14:59:59.999Z";
			String untilAfter = "2024-04-21T14:59:59.999Z";
			String res = mockMvc.perform(
					get("/scheduled_tasks").queryParam("start_before", startBefore)
						.cookie(cookies.toArray(new Cookie[0]))
						.queryParam("until_after", untilAfter))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
			List<ScheduledTaskResponseDto> response = ObjectMapperUtils.MAPPER.readValue(res,
				ObjectMapperUtils.MAPPER.getTypeFactory()
					.constructCollectionType(List.class, ScheduledTaskResponseDto.class));

			// Assert
			assertThat(response).isNotNull();
			assertThat(response.size()).isEqualTo(scheduledTasks.size());
		}

		@Test
		@DisplayName("DB에 ScheduledTask가 없는 경우 빈 리스트를 반환한다.")
		void noExist() throws Exception {
			// Act
			String startBefore = "2024-04-28T14:59:59.999Z";
			String untilAfter = "2024-04-21T14:59:59.999Z";
			String res = mockMvc.perform(
					get("/scheduled_tasks").queryParam("start_before", startBefore)
						.cookie(cookies.toArray(new Cookie[0]))
						.queryParam("until_after", untilAfter))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
			List<ScheduledTaskResponseDto> response = ObjectMapperUtils.MAPPER.readValue(res,
				ObjectMapperUtils.MAPPER.getTypeFactory()
					.constructCollectionType(List.class, ScheduledTaskResponseDto.class));

			// Assert
			assertThat(response).isEmpty();
		}

		@Test
		@DisplayName("start_before가 없는 경우 Bad Request 400 을 반환한다.")
		void noRequiredPropertyStartBefore() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.from(Instant.parse("2024-04-26T14:59:59.999Z"));
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.startFrom(timestamp)
				.until(timestamp)
				.build();
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			for (int i = 0; i < 10; i++) {
				ScheduledTask scheduledTask = ScheduledTask.builder()
					.taskRequest(taskRequestApi)
					.createdBy("user")
					.schedules(List.of(scheduledTaskSchedule))
					.build();
				em.persist(scheduledTask);
			}
			em.flush();
			em.clear();

			// Act
			// Assert
			String untilAfter = "2024-04-21T14:59:59.999Z";
			mockMvc.perform(get("/scheduled_tasks")
						.cookie(cookies.toArray(new Cookie[0]))
						.queryParam("until_after", untilAfter))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("until_after가 없는 경우 Bad Request 400 을 반환한다.")
		void noRequiredPropertyUntilAfter() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.from(Instant.parse("2024-04-26T14:59:59.999Z"));
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.startFrom(timestamp)
				.until(timestamp)
				.build();
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			for (int i = 0; i < 10; i++) {
				ScheduledTask scheduledTask = ScheduledTask.builder()
					.taskRequest(taskRequestApi)
					.createdBy("user")
					.schedules(List.of(scheduledTaskSchedule))
					.build();
				em.persist(scheduledTask);
			}
			em.flush();
			em.clear();

			// Act
			// Assert
			String startBefore = "2024-04-28T14:59:59.999Z";
			mockMvc.perform(get("/scheduled_tasks")
					.cookie(cookies.toArray(new Cookie[0]))
					.queryParam("start_before", startBefore))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Pagination - limit이 있는 경우 해당 개수만큼의 List<ScheduledTaskResponseDto>를 반환한다.")
		void paginationWithLimit() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.from(Instant.parse("2024-04-26T14:59:59.999Z"));
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.startFrom(timestamp)
				.until(timestamp)
				.build();
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			for (int i = 0; i < 10; i++) {
				ScheduledTask scheduledTask = ScheduledTask.builder()
					.taskRequest(taskRequestApi)
					.createdBy("user")
					.schedules(List.of(scheduledTaskSchedule))
					.build();
				em.persist(scheduledTask);
			}
			em.flush();
			em.clear();

			// Act
			String startBefore = "2024-04-28T14:59:59.999Z";
			String untilAfter = "2024-04-21T14:59:59.999Z";
			int limit = 3;
			String res = mockMvc.perform(get("/scheduled_tasks")
					.cookie(cookies.toArray(new Cookie[0]))
					.queryParam("start_before", startBefore)
					.queryParam("until_after", untilAfter)
					.queryParam("limit", String.valueOf(limit)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
			List<ScheduledTaskResponseDto> response = ObjectMapperUtils.MAPPER.readValue(res,
				ObjectMapperUtils.MAPPER.getTypeFactory()
					.constructCollectionType(List.class, ScheduledTaskResponseDto.class));

			// Assert
			assertThat(response).isNotNull();
			assertThat(response.size()).isEqualTo(limit);
		}

		@Test
		@DisplayName("Pagination - OrderBy가 id인 경우 해당 필드로 정렬된 리스트를 반환한다.")
		void paginationWithOrderById() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.from(Instant.parse("2024-04-26T14:59:59.999Z"));
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.startFrom(timestamp)
				.until(timestamp)
				.build();
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			List<ScheduledTask> scheduledTasks = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				ScheduledTask scheduledTask = ScheduledTask.builder()
					.taskRequest(taskRequestApi)
					.createdBy("user")
					.schedules(List.of(scheduledTaskSchedule))
					.build();
				scheduledTasks.add(scheduledTask);
				em.persist(scheduledTask);
			}
			em.flush();
			em.clear();

			// Act
			String startBefore = "2024-04-28T14:59:59.999Z";
			String untilAfter = "2024-04-21T14:59:59.999Z";
			String orderBy = "id";
			int limit = 3;
			String res = mockMvc.perform(get("/scheduled_tasks")
					.cookie(cookies.toArray(new Cookie[0]))
					.queryParam("start_before", startBefore)
					.queryParam("until_after", untilAfter)
					.queryParam("limit", String.valueOf(limit))
					.queryParam("order_by", orderBy))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
			List<ScheduledTaskResponseDto> response = ObjectMapperUtils.MAPPER.readValue(res,
				ObjectMapperUtils.MAPPER.getTypeFactory()
					.constructCollectionType(List.class, ScheduledTaskResponseDto.class));

			// Assert
			assertThat(response).isNotNull();
			assertThat(response.size()).isEqualTo(limit);
			assertThat(response.get(0).getId()).isEqualTo(scheduledTasks.get(0).getId());
			assertThat(response.get(1).getId()).isEqualTo(scheduledTasks.get(1).getId());
			assertThat(response.get(2).getId()).isEqualTo(scheduledTasks.get(2).getId());
		}

		@Test
		@DisplayName("Pagination - OrderBy가 -id인 경우 해당 필드로 역정렬된 리스트를 반환한다.")
		void paginationWithOrderByIdReverse() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.from(Instant.parse("2024-04-26T14:59:59.999Z"));
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.startFrom(timestamp)
				.until(timestamp)
				.build();
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			List<ScheduledTask> scheduledTasks = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				ScheduledTask scheduledTask = ScheduledTask.builder()
					.taskRequest(taskRequestApi)
					.createdBy("user")
					.schedules(List.of(scheduledTaskSchedule))
					.build();
				scheduledTasks.add(scheduledTask);
				em.persist(scheduledTask);
			}
			em.flush();
			em.clear();

			// Act
			String startBefore = "2024-04-28T14:59:59.999Z";
			String untilAfter = "2024-04-21T14:59:59.999Z";
			String orderBy = "-id";
			int limit = 3;
			String res = mockMvc.perform(get("/scheduled_tasks")
					.cookie(cookies.toArray(new Cookie[0]))
					.queryParam("start_before", startBefore)
					.queryParam("until_after", untilAfter)
					.queryParam("limit", String.valueOf(limit))
					.queryParam("order_by", orderBy))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
			List<ScheduledTaskResponseDto> response = ObjectMapperUtils.MAPPER.readValue(res,
				ObjectMapperUtils.MAPPER.getTypeFactory()
					.constructCollectionType(List.class, ScheduledTaskResponseDto.class));

			// Assert
			assertThat(response).isNotNull();
			assertThat(response.size()).isEqualTo(limit);
			assertThat(response.get(0).getId()).isEqualTo(scheduledTasks.get(scheduledTasks.size() - 1).getId());
			assertThat(response.get(1).getId()).isEqualTo(scheduledTasks.get(scheduledTasks.size() - 2).getId());
			assertThat(response.get(2).getId()).isEqualTo(scheduledTasks.get(scheduledTasks.size() - 3).getId());
		}

		@Test
		@DisplayName("Pagination - OrderBy가 created_by인 경우 해당 필드로 역정렬된 리스트를 반환한다.")
		void paginationWithOrderByCreatedBy() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.from(Instant.parse("2024-04-26T14:59:59.999Z"));
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.startFrom(timestamp)
				.until(timestamp)
				.build();
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			List<ScheduledTask> scheduledTasks = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				ScheduledTask scheduledTask = ScheduledTask.builder()
					.taskRequest(taskRequestApi)
					.createdBy("user" + i)
					.schedules(List.of(scheduledTaskSchedule))
					.build();
				scheduledTasks.add(scheduledTask);
				em.persist(scheduledTask);
			}
			em.flush();
			em.clear();

			// Act
			String startBefore = "2024-04-28T14:59:59.999Z";
			String untilAfter = "2024-04-21T14:59:59.999Z";
			String orderBy = "created_by";
			int limit = 3;
			String res = mockMvc.perform(get("/scheduled_tasks")
					.cookie(cookies.toArray(new Cookie[0]))
					.queryParam("start_before", startBefore)
					.queryParam("until_after", untilAfter)
					.queryParam("limit", String.valueOf(limit))
					.queryParam("order_by", orderBy))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
			List<ScheduledTaskResponseDto> response = ObjectMapperUtils.MAPPER.readValue(res,
				ObjectMapperUtils.MAPPER.getTypeFactory()
					.constructCollectionType(List.class, ScheduledTaskResponseDto.class));

			// Assert
			assertThat(response).isNotNull();
			assertThat(response.size()).isEqualTo(limit);
			assertThat(response.get(0).getCreatedBy()).isEqualTo(scheduledTasks.get(0).getCreatedBy());
			assertThat(response.get(1).getCreatedBy()).isEqualTo(scheduledTasks.get(1).getCreatedBy());
			assertThat(response.get(2).getCreatedBy()).isEqualTo(scheduledTasks.get(2).getCreatedBy());
		}

		@Test
		@DisplayName("Pagination - OrderBy가 -created_by인 경우 해당 필드로 역정렬된 리스트를 반환한다.")
		void paginationWithOrderByCreatedByReverse() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.from(Instant.parse("2024-04-26T14:59:59.999Z"));
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.startFrom(timestamp)
				.until(timestamp)
				.build();
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			List<ScheduledTask> scheduledTasks = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				ScheduledTask scheduledTask = ScheduledTask.builder()
					.taskRequest(taskRequestApi)
					.createdBy("user" + i)
					.schedules(List.of(scheduledTaskSchedule))
					.build();
				scheduledTasks.add(scheduledTask);
				em.persist(scheduledTask);
			}
			em.flush();
			em.clear();

			// Act
			String startBefore = "2024-04-28T14:59:59.999Z";
			String untilAfter = "2024-04-21T14:59:59.999Z";
			String orderBy = "-created_by";
			int limit = 3;
			String res = mockMvc.perform(get("/scheduled_tasks")
					.cookie(cookies.toArray(new Cookie[0]))
					.queryParam("start_before", startBefore)
					.queryParam("until_after", untilAfter)
					.queryParam("limit", String.valueOf(limit))
					.queryParam("order_by", orderBy))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
			List<ScheduledTaskResponseDto> response = ObjectMapperUtils.MAPPER.readValue(res,
				ObjectMapperUtils.MAPPER.getTypeFactory()
					.constructCollectionType(List.class, ScheduledTaskResponseDto.class));

			// Assert
			assertThat(response).isNotNull();
			assertThat(response.size()).isEqualTo(limit);
			assertThat(response.get(0).getCreatedBy()).isEqualTo(
				scheduledTasks.get(scheduledTasks.size() - 1).getCreatedBy());
			assertThat(response.get(1).getCreatedBy()).isEqualTo(
				scheduledTasks.get(scheduledTasks.size() - 2).getCreatedBy());
			assertThat(response.get(2).getCreatedBy()).isEqualTo(
				scheduledTasks.get(scheduledTasks.size() - 3).getCreatedBy());
		}

		@Test
		@DisplayName("Pagination - 해당하는 OrderBy가 없는 경우 기본 리스트를 반환한다.")
		void paginationWithNoOrderBy() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.from(Instant.parse("2024-04-26T14:59:59.999Z"));
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.startFrom(timestamp)
				.until(timestamp)
				.build();
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			for (int i = 0; i < 10; i++) {
				ScheduledTask scheduledTask = ScheduledTask.builder()
					.taskRequest(taskRequestApi)
					.createdBy("user" + i)
					.schedules(List.of(scheduledTaskSchedule))
					.build();
				em.persist(scheduledTask);
			}
			em.flush();
			em.clear();

			// Act
			String startBefore = "2024-04-28T14:59:59.999Z";
			String untilAfter = "2024-04-21T14:59:59.999Z";
			String orderBy = "no_option";
			int limit = 3;
			String res = mockMvc.perform(get("/scheduled_tasks")
					.cookie(cookies.toArray(new Cookie[0]))
					.queryParam("start_before", startBefore)
					.queryParam("until_after", untilAfter)
					.queryParam("limit", String.valueOf(limit))
					.queryParam("order_by", orderBy))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
			List<ScheduledTaskResponseDto> response = ObjectMapperUtils.MAPPER.readValue(res,
				ObjectMapperUtils.MAPPER.getTypeFactory()
					.constructCollectionType(List.class, ScheduledTaskResponseDto.class));

			// Assert
			assertThat(response).isNotNull();
			assertThat(response.size()).isEqualTo(limit);
		}

		@Test
		@DisplayName("Pagination - Offset이 있는 경우 해당 개수 만큼을 건너뛴 List<ScheduledTaskResponseDto>를 반환한다.")
		void paginationWithOffset() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.from(Instant.parse("2024-04-26T14:59:59.999Z"));
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.startFrom(timestamp)
				.until(timestamp)
				.build();
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			List<ScheduledTask> scheduledTasks = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				ScheduledTask scheduledTask = ScheduledTask.builder()
					.taskRequest(taskRequestApi)
					.createdBy("user")
					.schedules(List.of(scheduledTaskSchedule))
					.build();
				scheduledTasks.add(scheduledTask);
				em.persist(scheduledTask);
			}
			em.flush();
			em.clear();

			// Act
			String startBefore = "2024-04-28T14:59:59.999Z";
			String untilAfter = "2024-04-21T14:59:59.999Z";
			int limit = 3;
			int offset = 3;
			String orderBy = "id";
			String res = mockMvc.perform(get("/scheduled_tasks")
					.cookie(cookies.toArray(new Cookie[0]))
					.queryParam("start_before", startBefore)
					.queryParam("until_after", untilAfter)
					.queryParam("limit", String.valueOf(limit))
					.queryParam("offset", String.valueOf(offset))
					.queryParam("order_by", orderBy))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
			List<ScheduledTaskResponseDto> response = ObjectMapperUtils.MAPPER.readValue(res,
				ObjectMapperUtils.MAPPER.getTypeFactory()
					.constructCollectionType(List.class, ScheduledTaskResponseDto.class));

			// Assert
			assertThat(response).isNotNull();
			assertThat(response.size()).isEqualTo(limit);
			assertThat(response.get(0).getId()).isEqualTo(scheduledTasks.get(offset).getId());
			assertThat(response.get(1).getId()).isEqualTo(scheduledTasks.get(offset + 1).getId());
			assertThat(response.get(2).getId()).isEqualTo(scheduledTasks.get(offset + 2).getId());
		}
	}

	@Nested
	@DisplayName("POST /scheduled_tasks")
	class PostScheduledTask {
		@ParameterizedTest
		@ValueSource(strings = {"monday", "tuesday", "wednesday", "thursday", "friday",
			"saturday", "sunday", "day", "hour", "minute"})
		@DisplayName("ScheduledTask를 등록한다.")
		void createScheduledTask(String period) throws Exception {
			// Arrange
			TaskRequestApi taskRequest = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
			ScheduledTaskScheduleRequestDto schedule = ScheduledTaskScheduleRequestDto.builder()
				.startFrom(timestamp)
				.period(Period.valueOf(period))
				.at("18:44")
				.build();
			List<ScheduledTaskScheduleRequestDto> schedules = new ArrayList<>();
			schedules.add(schedule);
			CreateScheduledTaskDto createScheduledTaskDto = CreateScheduledTaskDto.builder()
				.schedules(schedules)
				.taskRequest(taskRequest)
				.build();
			String content = ObjectMapperUtils.MAPPER.writeValueAsString(createScheduledTaskDto);

			// Act
			String res = mockMvc.perform(post("/scheduled_tasks")
					.cookie(cookies.toArray(new Cookie[0]))
					.content(content)
					.contentType("application/json")
					.cookie(cookies.toArray(new Cookie[0])))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
			ScheduledTaskResponseDto response = ObjectMapperUtils.MAPPER.readValue(res, ScheduledTaskResponseDto.class);

			// Assert
			assertThat(response).isNotNull();
			assertThat(response.getTaskRequest()).isNotNull();
			assertThat(response.getTaskRequest().getRequester()).isEqualTo(taskRequest.getRequester());
			assertThat(response.getSchedules()).isNotNull();
			assertThat(response.getSchedules().size()).isEqualTo(1);
			assertThat(response.getSchedules().get(0).getPeriod()).isEqualTo(schedule.getPeriod());
			assertThat(response.getSchedules().get(0).getAt()).isEqualTo(schedule.getAt());
		}

		@Test
		@DisplayName("ScheduledTask를 등록한다. - task_request가 없는 경우")
		void createScheduledTaskWithNoBody() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
			ScheduledTaskScheduleRequestDto schedule = ScheduledTaskScheduleRequestDto.builder()
				.startFrom(timestamp)
				.period(Period.valueOf("monday"))
				.at("18:44")
				.build();
			List<ScheduledTaskScheduleRequestDto> schedules = new ArrayList<>();
			schedules.add(schedule);
			CreateScheduledTaskDto createScheduledTaskDto = CreateScheduledTaskDto.builder()
				.schedules(schedules)
				.build();
			String content = ObjectMapperUtils.MAPPER.writeValueAsString(createScheduledTaskDto);

			// Act
			// Assert
			mockMvc.perform(post("/scheduled_tasks")
					.content(content)
					.contentType("application/json")
					.cookie(cookies.toArray(new Cookie[0])))
				.andExpect(status().isBadRequest());
		}
	}

	@Nested
	@DisplayName("GET /scheduled_tasks/{task_id}")
	class GetScheduledTask {
		@Test
		@DisplayName("task_id에 해당하는 ScheduledTask를 조회한다.")
		void exist() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.startFrom(timestamp)
				.until(timestamp)
				.at("18:44")
				.period(Period.monday)
				.build();
			List<ScheduledTaskSchedule> schedules = new ArrayList<>();
			schedules.add(scheduledTaskSchedule);
			ScheduledTask scheduledTask = ScheduledTask.builder()
				.taskRequest(taskRequestApi)
				.createdBy("user")
				.schedules(schedules)
				.build();
			scheduledTaskSchedule.setScheduledTaskWith(scheduledTask);
			em.persist(scheduledTask);
			em.persist(scheduledTaskSchedule);
			em.flush();
			em.clear();

			// Act
			String res = mockMvc.perform(get("/scheduled_tasks/" + scheduledTask.getId())
					.cookie(cookies.toArray(new Cookie[0])))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			ScheduledTaskResponseDto response = ObjectMapperUtils.MAPPER.readValue(
				res, ScheduledTaskResponseDto.class);

			// Assert
			assertThat(response).isNotNull();
			assertThat(response.getTaskRequest()).isNotNull();
			assertThat(response.getTaskRequest().getRequester()).isEqualTo(taskRequestApi.getRequester());
			assertThat(response.getSchedules()).isNotNull();
			assertThat(response.getSchedules().size()).isEqualTo(1);
			assertThat(response.getSchedules().get(0).getPeriod()).isEqualTo(scheduledTaskSchedule.getPeriod());
			assertThat(response.getSchedules().get(0).getAt()).isEqualTo(scheduledTaskSchedule.getAt());
		}

		@Test
		@DisplayName("task_id에 해당하는 ScheduledTask가 없는 경우 404를 반환한다.")
		void noExist() throws Exception {
			// Arrange
			// Act
			// Assert
			mockMvc.perform(get("/scheduled_tasks/" + 1)
					.cookie(cookies.toArray(new Cookie[0])))
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("DELETE /scheduled_tasks/{task_id}")
	class DeleteScheduledTask {
		@Test
		@DisplayName("task_id에 해당하는 ScheduledTask를 삭제한다.")
		void exist() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.startFrom(timestamp)
				.at("18:44")
				.period(Period.monday)
				.build();
			List<ScheduledTaskSchedule> schedules = new ArrayList<>();
			schedules.add(scheduledTaskSchedule);
			ScheduledTask scheduledTask = ScheduledTask.builder()
				.taskRequest(taskRequestApi)
				.createdBy("user")
				.schedules(schedules)
				.build();
			scheduledTaskSchedule.setScheduledTaskWith(scheduledTask);
			em.persist(scheduledTask);
			em.persist(scheduledTaskSchedule);
			em.flush();
			em.clear();

			// Act
			String res = mockMvc.perform(delete("/scheduled_tasks/" + scheduledTask.getId())
					.cookie(cookies.toArray(new Cookie[0])))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

			// Assert
			assertThat(res).isEqualTo("success");
		}

		@Test
		@DisplayName("task_id에 해당하는 ScheduledTask가 없는 경우 404를 반환한다.")
		void noExist() throws Exception {
			// Arrange
			// Act
			// Assert
			mockMvc.perform(delete("/scheduled_tasks/" + 42)
					.cookie(cookies.toArray(new Cookie[0])))
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("POST /scheduled_tasks/{task_id}/update")
	class UpdateScheduledTask {
		@Test
		@DisplayName("except_date가 주어진 경우, exceptDates에 추가하고, 해당 날짜에 대한 새로운 ScheduledTask를 생성한다.")
		void existWithExceptDate() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.startFrom(timestamp)
				.at("18:44")
				.period(Period.monday)
				.build();
			List<ScheduledTaskSchedule> schedules = new ArrayList<>();
			schedules.add(scheduledTaskSchedule);
			ScheduledTask scheduledTask = ScheduledTask.builder()
				.taskRequest(taskRequestApi)
				.createdBy("user")
				.schedules(schedules)
				.exceptDates(new ArrayList<>())
				.build();
			scheduledTaskSchedule.setScheduledTaskWith(scheduledTask);
			em.persist(scheduledTask);
			em.persist(scheduledTaskSchedule);
			em.flush();
			em.clear();
			quartzService.scheduleTask(scheduledTask);

			ScheduledTaskScheduleRequestDto scheduledTaskScheduleRequestDto = ScheduledTaskScheduleRequestDto.builder()
				.startFrom(timestamp)
				.period(Period.monday)
				.at("12:42")
				.build();
			TaskRequestApi updatedTaskRequest = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester2")
				.build();
			UpdateScheduledTaskRequestDto createScheduledTaskRequestDto = UpdateScheduledTaskRequestDto.builder()
				.task_request(updatedTaskRequest)
				.schedules(List.of(scheduledTaskScheduleRequestDto))
				.build();
			String content = ObjectMapperUtils.MAPPER.writeValueAsString(createScheduledTaskRequestDto);
			String date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

			// Act
			String res = mockMvc.perform(post(String.format("/scheduled_tasks/%s/update", scheduledTask.getId()))
					.content(content)
					.contentType("application/json")
					.cookie(cookies.toArray(new Cookie[0]))
					.queryParam("except_date", date))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			ScheduledTaskResponseDto result = ObjectMapperUtils.MAPPER.readValue(
				res, ScheduledTaskResponseDto.class);

			// Assert
			assertThat(result).isNotNull();
			assertThat(result.getId()).isNotEqualTo(scheduledTask.getId());
			assertThat(result.getTaskRequest()).isNotNull();
			assertThat(result.getTaskRequest().getRequester()).isEqualTo(updatedTaskRequest.getRequester());
			assertThat(result.getSchedules()).isNotNull();
			assertThat(result.getSchedules().get(0).getAt()).isEqualTo("12:42");
			assertThat(result.getExceptDates()).isNull();
		}

		@Test
		@DisplayName("except_date가 주어진 경우, exceptDates에 추가하고, 해당 날짜에 대한 새로운 ScheduledTask를 생성한다."
			+ " - exceptDates가 없는 경우 리스트를 생성한다.")
		void existWithExceptDateNoExceptDates() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.startFrom(timestamp)
				.at("18:44")
				.period(Period.monday)
				.build();
			List<ScheduledTaskSchedule> schedules = new ArrayList<>();
			schedules.add(scheduledTaskSchedule);
			ScheduledTask scheduledTask = ScheduledTask.builder()
				.taskRequest(taskRequestApi)
				.createdBy("user")
				.schedules(schedules)
				.build();
			scheduledTaskSchedule.setScheduledTaskWith(scheduledTask);
			em.persist(scheduledTask);
			em.persist(scheduledTaskSchedule);
			em.flush();
			em.clear();
			quartzService.scheduleTask(scheduledTask);

			ScheduledTaskScheduleRequestDto scheduledTaskScheduleRequestDto = ScheduledTaskScheduleRequestDto.builder()
				.startFrom(timestamp)
				.period(Period.monday)
				.at("12:42")
				.build();
			TaskRequestApi updatedTaskRequest = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester2")
				.build();
			UpdateScheduledTaskRequestDto createScheduledTaskRequestDto = UpdateScheduledTaskRequestDto.builder()
				.task_request(updatedTaskRequest)
				.schedules(List.of(scheduledTaskScheduleRequestDto))
				.build();
			String content = ObjectMapperUtils.MAPPER.writeValueAsString(createScheduledTaskRequestDto);
			String date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

			// Act
			String res = mockMvc.perform(post(String.format("/scheduled_tasks/%s/update", scheduledTask.getId()))
					.content(content)
					.contentType("application/json")
					.cookie(cookies.toArray(new Cookie[0]))
					.queryParam("except_date", date))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			ScheduledTaskResponseDto result = ObjectMapperUtils.MAPPER.readValue(
				res, ScheduledTaskResponseDto.class);

			// Assert
			assertThat(result).isNotNull();
			assertThat(result.getId()).isNotEqualTo(scheduledTask.getId());
			assertThat(result.getTaskRequest()).isNotNull();
			assertThat(result.getTaskRequest().getRequester()).isEqualTo(updatedTaskRequest.getRequester());
			assertThat(result.getSchedules()).isNotNull();
			assertThat(result.getSchedules().get(0).getAt()).isEqualTo("12:42");
			assertThat(result.getExceptDates()).isNull();
		}

		@Test
		@DisplayName("except_date가 null인 경우 ScheduledTask를 수정한다.")
		void existWithNoExceptDate() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.startFrom(timestamp)
				.at("18:44")
				.period(Period.monday)
				.build();
			List<ScheduledTaskSchedule> schedules = new ArrayList<>();
			schedules.add(scheduledTaskSchedule);
			ScheduledTask scheduledTask = ScheduledTask.builder()
				.taskRequest(taskRequestApi)
				.createdBy("user")
				.schedules(schedules)
				.exceptDates(new ArrayList<>())
				.build();
			scheduledTaskSchedule.setScheduledTaskWith(scheduledTask);
			em.persist(scheduledTask);
			em.persist(scheduledTaskSchedule);
			em.flush();
			em.clear();
			quartzService.scheduleTask(scheduledTask);

			ScheduledTaskScheduleRequestDto scheduledTaskScheduleRequestDto = ScheduledTaskScheduleRequestDto.builder()
				.startFrom(timestamp)
				.period(Period.monday)
				.at("12:42")
				.build();
			TaskRequestApi updatedTaskRequest = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester2")
				.build();
			UpdateScheduledTaskRequestDto createScheduledTaskRequestDto = UpdateScheduledTaskRequestDto.builder()
				.task_request(updatedTaskRequest)
				.schedules(List.of(scheduledTaskScheduleRequestDto))
				.build();
			String content = ObjectMapperUtils.MAPPER.writeValueAsString(createScheduledTaskRequestDto);

			// Act
			String res = mockMvc.perform(post(String.format("/scheduled_tasks/%s/update", scheduledTask.getId()))
					.content(content)
					.contentType("application/json")
					.cookie(cookies.toArray(new Cookie[0])))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			ScheduledTaskResponseDto result = ObjectMapperUtils.MAPPER.readValue(
				res, ScheduledTaskResponseDto.class);

			// Assert
			assertThat(result).isNotNull();
			assertThat(result.getId()).isEqualTo(scheduledTask.getId());
			assertThat(result.getTaskRequest()).isNotNull();
			assertThat(result.getTaskRequest().getRequester()).isEqualTo(updatedTaskRequest.getRequester());
			assertThat(result.getSchedules()).isNotNull();
			assertThat(result.getSchedules().get(0).getAt()).isEqualTo("12:42");
			assertThat(result.getExceptDates().size()).isEqualTo(0);
		}

		@Test
		@DisplayName("except_date가 null인 경우 ScheduledTask를 수정한다. - exceptDates가 없는 경우 리스트를 생성한다.")
		void existWithNoExceptDateNoExceptDates() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.startFrom(timestamp)
				.at("18:44")
				.period(Period.monday)
				.build();
			List<ScheduledTaskSchedule> schedules = new ArrayList<>();
			schedules.add(scheduledTaskSchedule);
			ScheduledTask scheduledTask = ScheduledTask.builder()
				.taskRequest(taskRequestApi)
				.createdBy("user")
				.schedules(schedules)
				.build();
			scheduledTaskSchedule.setScheduledTaskWith(scheduledTask);
			em.persist(scheduledTask);
			em.persist(scheduledTaskSchedule);
			em.flush();
			em.clear();
			quartzService.scheduleTask(scheduledTask);

			ScheduledTaskScheduleRequestDto scheduledTaskScheduleRequestDto = ScheduledTaskScheduleRequestDto.builder()
				.startFrom(timestamp)
				.period(Period.monday)
				.at("12:42")
				.build();
			TaskRequestApi updatedTaskRequest = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester2")
				.build();
			UpdateScheduledTaskRequestDto createScheduledTaskRequestDto = UpdateScheduledTaskRequestDto.builder()
				.task_request(updatedTaskRequest)
				.schedules(List.of(scheduledTaskScheduleRequestDto))
				.build();
			String content = ObjectMapperUtils.MAPPER.writeValueAsString(createScheduledTaskRequestDto);

			// Act
			String res = mockMvc.perform(post(String.format("/scheduled_tasks/%s/update", scheduledTask.getId()))
					.content(content)
					.contentType("application/json")
					.cookie(cookies.toArray(new Cookie[0])))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			ScheduledTaskResponseDto result = ObjectMapperUtils.MAPPER.readValue(
				res, ScheduledTaskResponseDto.class);

			// Assert
			assertThat(result).isNotNull();
			assertThat(result.getId()).isEqualTo(scheduledTask.getId());
			assertThat(result.getTaskRequest()).isNotNull();
			assertThat(result.getTaskRequest().getRequester()).isEqualTo(updatedTaskRequest.getRequester());
			assertThat(result.getSchedules()).isNotNull();
			assertThat(result.getSchedules().get(0).getAt()).isEqualTo("12:42");
			assertThat(result.getExceptDates().size()).isEqualTo(0);
		}

		@Test
		@DisplayName("task_id에 해당하는 ScheduledTask가 없는 경우 404를 반환한다.")
		void noExist() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
			ScheduledTaskScheduleRequestDto scheduledTaskScheduleRequestDto = ScheduledTaskScheduleRequestDto.builder()
				.startFrom(timestamp)
				.until(timestamp)
				.period(Period.monday)
				.at("18:44")
				.build();
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			CreateScheduledTaskRequestDto createScheduledTaskRequestDto = CreateScheduledTaskRequestDto.builder()
				.task_request(taskRequestApi)
				.schedules(List.of(scheduledTaskScheduleRequestDto))
				.build();
			String content = ObjectMapperUtils.MAPPER.writeValueAsString(createScheduledTaskRequestDto);
			String date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

			// Act
			// Assert
			mockMvc.perform(post(String.format("/scheduled_tasks/%s/update", 1))
					.content(content)
					.contentType("application/json")
					.cookie(cookies.toArray(new Cookie[0]))
					.queryParam("except_date", date))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("task_id에 해당하는 ScheduledTask를 수정할 때 except_date의 형식이 잘못된 경우 400을 반환한다.")
		void invalidExceptDate() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
			ScheduledTaskScheduleRequestDto scheduledTaskScheduleRequestDto = ScheduledTaskScheduleRequestDto.builder()
				.startFrom(timestamp)
				.until(timestamp)
				.period(Period.monday)
				.at("18:44")
				.build();
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			CreateScheduledTaskRequestDto createScheduledTaskRequestDto = CreateScheduledTaskRequestDto.builder()
				.task_request(taskRequestApi)
				.schedules(List.of(scheduledTaskScheduleRequestDto))
				.build();
			String content = ObjectMapperUtils.MAPPER.writeValueAsString(createScheduledTaskRequestDto);

			// Act
			// Assert
			mockMvc.perform(post(String.format("/scheduled_tasks/%s/update", 1))
					.queryParam("except_date", "invalid format")
					.contentType("application/json")
					.cookie(cookies.toArray(new Cookie[0]))
					.content(content))
				.andExpect(status().isBadRequest());
		}
	}

	@Nested
	@DisplayName("PUT /scheduled_tasks/{task_id}/clear")
	class ClearScheduledTask {
		@Test
		@DisplayName("task_id에 해당하는 ScheduledTask의 스케쥴링을 중단하고 exceptDates에 eventDate를 추가하고 다시 스케쥴링한다.")
		void clearExist() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.startFrom(timestamp)
				.at("18:44")
				.period(Period.monday)
				.build();
			List<ScheduledTaskSchedule> schedules = new ArrayList<>();
			schedules.add(scheduledTaskSchedule);
			ScheduledTask scheduledTask = ScheduledTask.builder()
				.taskRequest(taskRequestApi)
				.createdBy("user")
				.schedules(schedules)
				.exceptDates(new ArrayList<>())
				.build();
			scheduledTaskSchedule.setScheduledTaskWith(scheduledTask);
			em.persist(scheduledTask);
			em.persist(scheduledTaskSchedule);
			em.flush();
			em.clear();
			quartzService.scheduleTask(scheduledTask);
			String date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

			// Act
			String res = mockMvc.perform(put(String.format("/scheduled_tasks/%s/clear", scheduledTask.getId()))
					.cookie(cookies.toArray(new Cookie[0]))
					.queryParam("event_date", date))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			JobKey jobKey = JobKey.jobKey(String.valueOf(scheduledTask.getId()), ScheduledTaskJob.GROUP);
			scheduler.checkExists(jobKey);

			// Assert
			assertThat(res).isEqualTo("success");
			assertThat(scheduler.checkExists(jobKey)).isTrue();
		}

		@Test
		@DisplayName("event_date가 없는 경우 Bad Request 400 을 반환한다.")
		void clearWithNoEventDate() throws Exception {
			// Arrange
			Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.startFrom(timestamp)
				.at("18:44")
				.period(Period.monday)
				.build();
			List<ScheduledTaskSchedule> schedules = new ArrayList<>();
			schedules.add(scheduledTaskSchedule);
			ScheduledTask scheduledTask = ScheduledTask.builder()
				.taskRequest(taskRequestApi)
				.createdBy("user")
				.schedules(schedules)
				.exceptDates(new ArrayList<>())
				.build();
			scheduledTaskSchedule.setScheduledTaskWith(scheduledTask);
			em.persist(scheduledTask);
			em.persist(scheduledTaskSchedule);
			em.flush();
			em.clear();
			quartzService.scheduleTask(scheduledTask);

			// Act
			// Assert
			mockMvc.perform(put(String.format("/scheduled_tasks/%s/clear", scheduledTask.getId()))
					.cookie(cookies.toArray(new Cookie[0])))
				.andExpect(status().isBadRequest());
		}
	}
}
