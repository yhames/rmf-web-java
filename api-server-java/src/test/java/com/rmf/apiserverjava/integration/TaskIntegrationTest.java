package com.rmf.apiserverjava.integration;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmf.apiserverjava.config.annotation.IntegrationTest;
import com.rmf.apiserverjava.entity.tasks.TaskEventLog;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogLog;
import com.rmf.apiserverjava.entity.tasks.TaskRequest;
import com.rmf.apiserverjava.entity.tasks.TaskState;
import com.rmf.apiserverjava.rmfapi.Tier;
import com.rmf.apiserverjava.rmfapi.tasks.TaskEventLogApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskRequestApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskStateApi;

import jakarta.persistence.EntityManager;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class TaskIntegrationTest {

	@Autowired
	EntityManager em;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Nested
	@DisplayName("GET /tasks/{taskId}/request")
	class GetTaskRequest {
		@Test
		@DisplayName("존재하지 않는 taskId로 요청하면 404 에러코드와 Not found 에러메시지를 반환한다.")
		void getTaskRequestNotFound() throws Exception {
			//Arrange
			String taskId = "not_exist_task_id";

			//Act
			String content = mockMvc.perform(get("/tasks/{taskId}/request", taskId))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();

			//Assert
			assertThat(content).contains("Not found");
		}

		@Test
		@DisplayName("존재하는 taskId로 요청하면 해당 taskRequest 데이터를 반환한다.")
		void getTaskRequestFound() throws Exception {
			//Arrange
			String id = "t1";
			TaskRequestApi req = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			TaskRequest taskRequest = TaskRequest.builder().id(id).request(req).build();
			em.persist(taskRequest);
			em.flush();
			em.clear();

			//Act
			String rep = mockMvc.perform(get("/tasks/{taskId}/request", taskRequest.getId()))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			TaskRequestApi content = objectMapper.readValue(rep, TaskRequestApi.class);
			//Assert
			assertThat(content).isNotNull();
			assertThat(content.getUnixMillisEarliestStartTime())
				.isEqualTo(req.getUnixMillisEarliestStartTime());
			assertThat(content.getUnixMillisRequestTime()).isEqualTo(req.getUnixMillisRequestTime());
			assertThat(content.getPriority().get("key")).isEqualTo(req.getPriority().get("key"));
			assertThat(content.getLabels()).isEqualTo(req.getLabels());
			assertThat(content.getRequester()).isEqualTo(req.getRequester());
		}
	}

	@Nested
	@DisplayName("GET /tasks")
	class GetTaskStateList {
		@Test
		@DisplayName("taskState가 존재하지 않을 경우 빈 목록을 반환한다.")
		void getTaskStateListNotFound() throws Exception {
			//Arrange
			//Act
			String response = mockMvc.perform(get("/tasks").param("task_id", "not_exist_task_id"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			//Assert
			assertThat(response).isEqualTo("[]");
		}

		@Test
		@DisplayName("taskState가 존재할 경우 taskStateApi 목록을 반환한다.")
		void getTaskStateListFound() throws Exception {
			//Arrange
			String id = "t1";
			String category = "category";
			String assignedTo = "assignedTo";
			String status = "status";
			TaskStateApi state = TaskStateApi.builder().build();
			TaskState taskState = TaskState.builder()
				.id(id).data(state)
				.category(category)
				.unixMillisStartTime(0L)
				.unixMillisFinishTime(10000L)
				.unixMillisRequestTime(10000L)
				.assignedTo(assignedTo)
				.status(status)
				.build();
			em.persist(taskState);
			em.flush();
			em.clear();

			//Act
			String rep = mockMvc.perform(get("/tasks"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			TaskStateApi[] content = objectMapper.readValue(rep, TaskStateApi[].class);

			//Assert
			assertThat(content).isNotNull();
			assertThat(content.length).isEqualTo(1);
		}

		@Test
		@DisplayName("올바른 쿼리파라미터가 주어질 경우 조건에 맞추어 taskStateApi 목록을 반환한다.")
		void getTaskStateListFoundInQuery() throws Exception {
			//Arrange
			String id = "t1";
			String category = "category";
			String assignedTo = "assignedTo";
			String status = "status";
			TaskStateApi state = TaskStateApi.builder().build();
			TaskState taskState = TaskState.builder()
				.id(id).data(state)
				.category(category)
				.unixMillisStartTime(0L)
				.unixMillisFinishTime(10000L)
				.unixMillisRequestTime(10000L)
				.assignedTo(assignedTo)
				.status(status)
				.build();
			em.persist(taskState);
			em.flush();
			em.clear();

			//Act
			String rep = mockMvc.perform(get("/tasks")
					.param("task_id", id)
					.param("category", category)
					.param("assigned_to", assignedTo)
					.param("status", status)
					.param("start_time_between", "0,0")
					.param("finish_time_between", "10000,10000")
				)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			TaskStateApi[] content = objectMapper.readValue(rep, TaskStateApi[].class);

			//Assert
			assertThat(content).isNotNull();
			assertThat(content.length).isEqualTo(1);
		}

		@Test
		@DisplayName("올바르지 않은 쿼리파라미터가 주어질 경우 조건을 무시하고 전체 목록을 반환한다")
		void getTaskStateListWithBadParam() throws Exception {
			//Arrange
			String id = "t1";
			String category = "category";
			String assignedTo = "assignedTo";
			String status = "status";
			TaskStateApi state = TaskStateApi.builder().build();
			TaskState taskState = TaskState.builder()
				.id(id).data(state)
				.category(category)
				.unixMillisStartTime(0L)
				.unixMillisFinishTime(10000L)
				.unixMillisRequestTime(10000L)
				.assignedTo(assignedTo)
				.status(status)
				.build();
			em.persist(taskState);
			em.flush();
			em.clear();

			//Act
			String rep = mockMvc.perform(get("/tasks")
					.param("unknown", "unknown")
					.param("limit", "-1")
					.param("offset", "-1")
					.param("order_by", "unknown")
				)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			TaskStateApi[] content = objectMapper.readValue(rep, TaskStateApi[].class);

			//Assert
			assertThat(content).isNotNull();
			assertThat(content.length).isEqualTo(1);
		}


		@Test
		@DisplayName("페이징 조건을 주어서 taskStateApi 목록을 반환한다. 정렬 조건에 따라 순서가 바뀐다.")
		void getTaskStateListWithPaging() throws Exception {
			//Arrange
			TaskStateApi state = TaskStateApi.builder().build();
			TaskState taskState1 = TaskState.builder()
				.id("t1")
				.unixMillisStartTime(1L)
				.data(state)
				.build();
			TaskState taskState2 = TaskState.builder()
				.id("t2")
				.unixMillisStartTime(2L)
				.data(state)
				.build();
			em.persist(taskState1);
			em.persist(taskState2);
			em.flush();
			em.clear();

			//Act
			String rep1 = mockMvc.perform(get("/tasks")
					.param("limit", "2")
					.param("offset", "0")
					.param("order_by",
						"category,assigned_to,unix_millis_start_time,unix_millis_finish_time,status,task_id")
				)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			TaskStateApi[] content1 = objectMapper.readValue(rep1, TaskStateApi[].class);

			String rep2 = mockMvc.perform(get("/tasks")
					.param("limit", "2")
					.param("offset", "0")
					.param("order_by",
						"-category,-assigned_to,-unix_millis_start_time,-unix_millis_finish_time,-status,-task_id")
				)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			TaskStateApi[] content2 = objectMapper.readValue(rep2, TaskStateApi[].class);

			//Assert
			assertThat(content1).isNotNull();
			assertThat(content2).isNotNull();
			assertThat(content1.length).isEqualTo(2);
			assertThat(content2.length).isEqualTo(2);
			assertThat(content1[0].getUnixMillisStartTime()).isEqualTo(content2[1].getUnixMillisStartTime());
			assertThat(content1[1].getUnixMillisStartTime()).isEqualTo(content2[0].getUnixMillisStartTime());

		}
	}

	@Nested
	@DisplayName("GET /tasks/{taskId}/state")
	class GetTaskState {
		@Test
		@DisplayName("존재하지 않는 taskId로 요청하면 404 에러코드와 Not found 에러메시지를 반환한다.")
		void getTaskStateNotFound() throws Exception {
			//Arrange
			String taskId = "not_exist_task_id";

			//Act
			String content = mockMvc.perform(get("/tasks/{taskId}/state", taskId))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();

			//Assert
			assertThat(content).contains("Not found");
		}

		@Test
		@DisplayName("존재하는 taskId로 요청하면 해당 taskStateApi 데이터를 반환한다.")
		void getTaskStateFound() throws Exception {
			//Arrange
			String id = "t1";
			TaskStateApi state = TaskStateApi.builder().build();
			TaskState taskState = TaskState.builder().id(id).data(state).build();
			em.persist(taskState);
			em.flush();
			em.clear();

			//Act
			String rep = mockMvc.perform(get("/tasks/{taskId}/state", taskState.getId()))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			TaskStateApi content = objectMapper.readValue(rep, TaskStateApi.class);

			//Assert
			assertThat(content).isNotNull();
		}
	}

	@Nested
	@DisplayName("GET /tasks/{taskId}/log")
	class GetTaskLog {
		@Test
		@DisplayName("존재하지 않는 taskId로 요청하면 404 에러코드와 Not found 에러메시지를 반환한다.")
		void getTaskLogNotFound() throws Exception {
			//Arrange
			String taskId = "not_exist_task_id";

			//Act
			String content = mockMvc.perform(get("/tasks/{taskId}/log", taskId))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();

			//Assert
			assertThat(content).contains("Not found");
		}

		@Test
		@DisplayName("존재하는 taskId로 요청하면 해당 taskEventLogApi 데이터를 반환한다.")
		void getTaskLogFound() throws Exception {
			//Arrange
			String id = "t1";
			TaskEventLog taskLog = new TaskEventLog(id);
			em.persist(taskLog);
			em.flush();
			em.clear();

			//Act
			String content = mockMvc.perform(get("/tasks/{taskId}/log", taskLog.getTaskId()))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			//Assert
			assertThat(content).isNotNull();
			assertThat(content).contains("task_id");
		}

		@Test
		@DisplayName("쿼리파라미터로 주어진 시간 범위에 해당하는 taskEventLogApi 데이터를 반환한다.")
		void getTaskLogWithTimeRange() throws Exception {
			//Arrange
			TaskEventLog taskLog = TaskEventLog.builder()
				.taskId("t1")
				.build();
			em.persist(taskLog);
			for (int i = 0; i < 10; i++) {
				TaskEventLogLog log = TaskEventLogLog.builder()
					.unixMillisTime(i * 1000)
					.tier(Tier.info)
					.text("text")
					.seq(i)
					.build();
				log.setTaskEventLogWithoutRelation(taskLog);
				em.persist(log);
			}

			//Act
			String content = mockMvc.perform(get("/tasks/{taskId}/log", "t1")
					.param("between", "2000,4000")
				)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			//Assert
			assertThat(content).isNotNull();
			assertThat(content).contains("task_id");
			TaskEventLogApi taskEventLogApi = objectMapper.readValue(content, TaskEventLogApi.class);
			assertThat(taskEventLogApi.getLog().size()).isEqualTo(3);
			taskEventLogApi.getLog().forEach(log -> {
				assertThat(log.getUnixMillisTime()).isBetween(2000L, 4000L);
			});
		}
	}
}
