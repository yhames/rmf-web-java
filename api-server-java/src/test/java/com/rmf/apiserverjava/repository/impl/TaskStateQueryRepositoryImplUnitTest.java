package com.rmf.apiserverjava.repository.impl;

import static com.rmf.apiserverjava.global.constant.ProfileConstant.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.dto.page.PaginationDto;
import com.rmf.apiserverjava.dto.tasks.TaskStatesQueryDto;
import com.rmf.apiserverjava.dto.time.TimeRangeDto;
import com.rmf.apiserverjava.entity.tasks.TaskState;
import com.rmf.apiserverjava.global.parser.LogBetweenParser;
import com.rmf.apiserverjava.rmfapi.tasks.TaskStateApi;

import jakarta.persistence.EntityManager;

@UnitTest
@Transactional
@SpringBootTest
@ActiveProfiles(TEST_WITHOUT_SECURITY)
class TaskStateQueryRepositoryImplUnitTest {

	@Autowired
	EntityManager em;

	@Autowired
	TaskStateQueryRepositoryImpl taskStateQueryRepositoryImpl;

	@Mock
	LogBetweenParser logBetweenParser;

	@Nested
	@DisplayName("findAllTaskStateByQuery")
	class FindAllTaskStateByQuery {

		@BeforeEach
		void setUp() {
			TaskStateApi stateApi = mock(TaskStateApi.class);
			TaskState taskStateById
				= TaskState.builder().id("id").data(stateApi).build();
			TaskState taskStateByCategory
				= TaskState.builder().id("t1").data(stateApi).category("category").build();
			TaskState taskStateByAssignedTo
				= TaskState.builder().id("t2").data(stateApi).assignedTo("assignedTo").build();
			TaskState taskStateByStartTime
				= TaskState.builder().id("t3").data(stateApi).unixMillisStartTime(0L).build();
			TaskState taskStateByFinishTime
				= TaskState.builder().id("t4").data(stateApi).unixMillisFinishTime(0L).build();
			TaskState taskStateByStatus
				= TaskState.builder().id("t5").data(stateApi).status("status").build();

			em.persist(taskStateById);
			em.persist(taskStateByCategory);
			em.persist(taskStateByAssignedTo);
			em.persist(taskStateByStartTime);
			em.persist(taskStateByFinishTime);
			em.persist(taskStateByStatus);
			em.flush();
			em.clear();
		}

		@Test
		@DisplayName("taskId를 이용하여 TaskStateList를 조회한다.")
		void findAllTaskStateByTaskId() {
			// Arrange
			TaskStatesQueryDto taskStatesQueryDto = TaskStatesQueryDto.builder().id(Arrays.asList("id")).build();
			// Act
			List<TaskState> allTaskStateByQuery
				= taskStateQueryRepositoryImpl.findAllTaskStateByQuery(taskStatesQueryDto, mock(PaginationDto.class));
			// Assert
			assertThat(allTaskStateByQuery.size()).isEqualTo(1);
			assertThat(allTaskStateByQuery.get(0).getId()).isEqualTo("id");
		}

		@Test
		@DisplayName("category를 이용하여 TaskStateList를 조회한다.")
		void findAllTaskStateByCategory() {
			// Arrange
			TaskStatesQueryDto taskStatesQueryDto
				= TaskStatesQueryDto.builder().category(Arrays.asList("category")).build();
			// Act
			List<TaskState> allTaskStateByQuery
				= taskStateQueryRepositoryImpl.findAllTaskStateByQuery(taskStatesQueryDto, mock(PaginationDto.class));
			// Assert
			assertThat(allTaskStateByQuery.size()).isEqualTo(1);
			assertThat(allTaskStateByQuery.get(0).getCategory()).isEqualTo("category");
		}

		@Test
		@DisplayName("assignedTo를 이용하여 TaskStateList를 조회한다.")
		void findAllTaskStateByAssignedTo() {
			// Arrange
			TaskStatesQueryDto taskStatesQueryDto
				= TaskStatesQueryDto.builder().assignedTo(Arrays.asList("assignedTo")).build();
			// Act
			List<TaskState> allTaskStateByQuery
				= taskStateQueryRepositoryImpl.findAllTaskStateByQuery(taskStatesQueryDto, mock(PaginationDto.class));
			// Assert
			assertThat(allTaskStateByQuery.size()).isEqualTo(1);
			assertThat(allTaskStateByQuery.get(0).getAssignedTo()).isEqualTo("assignedTo");
		}

		@Test
		@DisplayName("startTimeBetween을 이용하여 TaskStateList를 조회한다.")
		void findAllTaskStateByStartTimeBetween() {
			// Arrange
			TimeRangeDto timeRangeDto = new TimeRangeDto(0L, 10000L);
			TaskStatesQueryDto taskStatesQueryDto
				= TaskStatesQueryDto.builder().startTimeBetween(timeRangeDto).build();
			// Act
			List<TaskState> allTaskStateByQuery
				= taskStateQueryRepositoryImpl.findAllTaskStateByQuery(taskStatesQueryDto, mock(PaginationDto.class));
			// Assert
			assertThat(allTaskStateByQuery.size()).isEqualTo(1);
			assertThat(allTaskStateByQuery.get(0).getUnixMillisStartTime()).isEqualTo(new Timestamp(0L));
		}

		@Test
		@DisplayName("finishTimeBetween을 이용하여 TaskStateList를 조회한다.")
		void findAllTaskStateByFinishTimeBetween() {
			// Arrange
			TimeRangeDto timeRangeDto = new TimeRangeDto(0L, 10000L);
			TaskStatesQueryDto taskStatesQueryDto
				= TaskStatesQueryDto.builder().finishTimeBetween(timeRangeDto).build();
			// Act
			List<TaskState> allTaskStateByQuery
				= taskStateQueryRepositoryImpl.findAllTaskStateByQuery(taskStatesQueryDto, mock(PaginationDto.class));
			// Assert
			assertThat(allTaskStateByQuery.size()).isEqualTo(1);
			assertThat(allTaskStateByQuery.get(0).getUnixMillisFinishTime()).isEqualTo(new Timestamp(0L));
		}

		@Test
		@DisplayName("status를 이용하여 TaskStateList를 조회한다.")
		void findAllTaskStateByStatus() {
			// Arrange
			TaskStatesQueryDto taskStatesQueryDto
				= TaskStatesQueryDto.builder().status(Arrays.asList("status")).build();
			// Act
			List<TaskState> allTaskStateByQuery
				= taskStateQueryRepositoryImpl.findAllTaskStateByQuery(taskStatesQueryDto, mock(PaginationDto.class));
			// Assert
			assertThat(allTaskStateByQuery.size()).isEqualTo(1);
			assertThat(allTaskStateByQuery.get(0).getStatus()).isEqualTo("status");
		}
	}
}
