package com.rmf.apiserverjava.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.quartz.SchedulerException;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.dto.scheduledtasks.CreateScheduledTaskDto;
import com.rmf.apiserverjava.dto.scheduledtasks.ScheduledTaskPaginationDto;
import com.rmf.apiserverjava.dto.scheduledtasks.ScheduledTaskScheduleRequestDto;
import com.rmf.apiserverjava.dto.scheduledtasks.ScheduledTaskSearchCondition;
import com.rmf.apiserverjava.dto.scheduledtasks.UpdateScheduledTaskDto;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTask;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTaskSchedule;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTaskSchedule.Period;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.exception.custom.NotFoundException;
import com.rmf.apiserverjava.repository.ScheduledTaskRepository;
import com.rmf.apiserverjava.repository.ScheduledTaskScheduleRepository;
import com.rmf.apiserverjava.rmfapi.tasks.TaskRequestApi;
import com.rmf.apiserverjava.service.QuartzService;
import com.rmf.apiserverjava.service.TaskService;

@UnitTest
class ScheduledTaskServiceImplUnitTest {

	@Mock
	ScheduledTaskRepository scheduledTaskRepository;

	@Mock
	ScheduledTaskScheduleRepository scheduledTaskScheduleRepository;

	@Mock
	TaskService taskService;

	@Mock
	QuartzService quartzService;

	@InjectMocks
	ScheduledTaskServiceImpl scheduledTaskService;

	@Nested
	@DisplayName("GetScheduledTasks")
	class GetScheduledTasks {

		@ParameterizedTest
		@ValueSource(strings = {"id", "-id", "created_by", "-created_by", "no_option"})
		@DisplayName("조회에 성공할 경우 List<ScheduledTask>를 반환한다.")
		void getScheduledTasks(String orderBy) {
			// Arrange
			Timestamp timestamp = Timestamp.from(Instant.parse("2024-04-26T14:59:59.999Z"));
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.startFrom(timestamp)
				.until(timestamp)
				.build();
			List<ScheduledTask> scheduledTasks = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				ScheduledTask scheduledTask = ScheduledTask.builder()
					.id(i)
					.createdBy("user")
					.schedules(List.of(scheduledTaskSchedule))
					.build();
				scheduledTasks.add(scheduledTask);
			}

			ScheduledTaskPaginationDto pagination = ScheduledTaskPaginationDto.builder()
				.start_before("2024-04-28T14:59:59.999Z")
				.until_after("2024-04-21T15:00:00.000Z")
				.order_by(orderBy)
				.build();
			ScheduledTaskSearchCondition condition = ScheduledTaskSearchCondition.MapStruct.INSTANCE.toDto(pagination);

			when(scheduledTaskRepository.searchStartBeforeUntilAfter(condition)).thenReturn(scheduledTasks);

			// Act
			List<ScheduledTask> result = scheduledTaskService.getScheduledTasks(condition);

			// Assert
			assertThat(result).isNotEmpty();
			assertThat(result.size()).isEqualTo(scheduledTasks.size());
		}
	}

	@Nested
	@DisplayName("PostScheduledTask")
	class PostScheduledTask {
		@Test
		@DisplayName("등록에 성공할 경우 Optional<ScheduledTask>를 반환한다.")
		void saveScheduledTask() throws SchedulerException {
			// Arrange
			ScheduledTask scheduledTask = mock(ScheduledTask.class);
			ScheduledTaskSchedule scheduledTaskSchedule = mock(ScheduledTaskSchedule.class);
			User user = User.builder().username("stub").isAdmin(true).build();

			when(scheduledTaskRepository.save(any(ScheduledTask.class))).thenReturn(scheduledTask);
			when(scheduledTaskScheduleRepository.saveAll(anyList())).thenReturn(List.of(scheduledTaskSchedule));
			doNothing().when(quartzService).scheduleTask(any(ScheduledTask.class));

			Timestamp timestamp = Timestamp.from(Instant.parse("2024-04-26T14:59:59.999Z"));
			List<ScheduledTaskScheduleRequestDto> schedules = new ArrayList<>();
			ScheduledTaskScheduleRequestDto scheduledTaskScheduleRequestDto = ScheduledTaskScheduleRequestDto.builder()
				.startFrom(timestamp)
				.until(timestamp)
				.every((short)1)
				.period(Period.monday)
				.at("18:44")
				.build();
			schedules.add(scheduledTaskScheduleRequestDto);
			CreateScheduledTaskDto createScheduledTaskDto = CreateScheduledTaskDto.builder()
				.taskRequest(mock(TaskRequestApi.class))
				.schedules(schedules)
				.build();

			// Act
			Optional<ScheduledTask> result = scheduledTaskService.postScheduledTask(createScheduledTaskDto, user);

			// Assert
			assertThat(result.isPresent()).isTrue();
			assertThat(result.get()).isEqualTo(scheduledTask);
		}

		@Test
		@DisplayName("every가 null인 경우에도 등록에 성공한다.")
		void saveScheduledTaskWithNoEvery() throws SchedulerException {
			// Arrange
			ScheduledTask scheduledTask = mock(ScheduledTask.class);
			ScheduledTaskSchedule scheduledTaskSchedule = mock(ScheduledTaskSchedule.class);

			when(scheduledTaskRepository.save(any(ScheduledTask.class))).thenReturn(scheduledTask);
			when(scheduledTaskScheduleRepository.saveAll(anyList())).thenReturn(List.of(scheduledTaskSchedule));
			doNothing().when(quartzService).scheduleTask(any(ScheduledTask.class));

			Timestamp timestamp = Timestamp.from(Instant.parse("2024-04-26T14:59:59.999Z"));
			List<ScheduledTaskScheduleRequestDto> schedules = new ArrayList<>();
			ScheduledTaskScheduleRequestDto scheduledTaskScheduleRequestDto = ScheduledTaskScheduleRequestDto.builder()
				.startFrom(timestamp)
				.until(timestamp)
				.period(Period.monday)
				.at("18:44")
				.build();
			schedules.add(scheduledTaskScheduleRequestDto);
			CreateScheduledTaskDto createScheduledTaskDto = CreateScheduledTaskDto.builder()
				.taskRequest(mock(TaskRequestApi.class))
				.schedules(schedules)
				.build();
			User user = User.builder().username("stub").isAdmin(true).build();

			// Act
			Optional<ScheduledTask> result = scheduledTaskService.postScheduledTask(createScheduledTaskDto, user);

			// Assert
			assertThat(result.isPresent()).isTrue();
			assertThat(result.get()).isEqualTo(scheduledTask);
		}
	}

	@Nested
	@DisplayName("GetScheduledTask")
	class GetScheduledTask {
		@Test
		@DisplayName("task_id에 해당하는 ScheduledTask 조회에 성공할 경우 Optional<ScheduledTask>를 반환한다.")
		void getScheduledTaskFound() {
			// Arrange
			ScheduledTask scheduledTask = mock(ScheduledTask.class);
			when(scheduledTaskRepository.findById(anyInt())).thenReturn(Optional.of(scheduledTask));

			// Act
			Optional<ScheduledTask> result = scheduledTaskService.getScheduledTask(1);

			// Assert
			assertThat(result.isPresent()).isTrue();
		}

		@Test
		@DisplayName("task_id에 해당하는 ScheduledTask 조회에 실패할 경우 Optional.empty()를 반환한다.")
		void getScheduledTaskNotFound() {
			// Arrange
			when(scheduledTaskRepository.findById(anyInt())).thenReturn(Optional.empty());

			// Act
			Optional<ScheduledTask> result = scheduledTaskService.getScheduledTask(1);

			// Assert
			assertThat(result.isEmpty()).isTrue();
		}
	}

	@Nested
	@DisplayName("DeleteScheduledTask")
	class DeleteScheduledTask {
		@Test
		@DisplayName("task_id에 해당하는 ScheduledTask 삭제에 성공할 경우 sucess를 반환한다.")
		void deleteScheduledFound() throws SchedulerException {
			// Arrange
			ScheduledTask scheduledTask = mock(ScheduledTask.class);
			when(scheduledTaskRepository.findById(anyInt())).thenReturn(Optional.of(scheduledTask));
			doNothing().when(quartzService).unScheduleTask(any(ScheduledTask.class));
			doNothing().when(scheduledTaskRepository).delete(any(ScheduledTask.class));

			// Act
			// Assert
			scheduledTaskService.deleteScheduledTask(1);
		}

		@Test
		@DisplayName("task_id에 해당하는 ScheduledTask가 없을 경우 NotFoundException을 반환한다.")
		void deleteScheduledNotFound() {
			// Arrange
			when(scheduledTaskRepository.findById(anyInt())).thenReturn(Optional.empty());

			// Act
			// Assert
			Assertions.assertThrows(NotFoundException.class,
				() -> scheduledTaskService.deleteScheduledTask(1));
		}
	}

	@Nested
	@DisplayName("UpdateScheduledTask")
	class UpdateScheduledTask {
		@Test
		@DisplayName("task_id에 해당하는 ScheduledTask 수정에 성공할 경우 Optional<ScheduledTask>를 반환한다.")
		void updateScheduledTaskFound() throws SchedulerException {
			// Arrange
			ScheduledTask scheduledTask = mock(ScheduledTask.class);

			when(scheduledTaskRepository.findById(anyInt())).thenReturn(Optional.of(scheduledTask));
			doNothing().when(quartzService).scheduleTask(any(ScheduledTask.class));

			TaskRequestApi taskRequestApi = TaskRequestApi.builder()
				.unixMillisEarliestStartTime(System.currentTimeMillis())
				.unixMillisRequestTime(System.currentTimeMillis())
				.priority(Map.of("key", "value"))
				.labels(Arrays.asList("label1", "label2"))
				.requester("requester")
				.build();
			Timestamp timestamp = Timestamp.from(Instant.parse("2024-04-26T14:59:59.999Z"));
			ScheduledTaskScheduleRequestDto scheduledTaskScheduleRequestDto = ScheduledTaskScheduleRequestDto
				.builder()
				.startFrom(timestamp)
				.until(timestamp)
				.period(Period.monday)
				.build();
			UpdateScheduledTaskDto updateScheduledTaskDto = UpdateScheduledTaskDto.builder()
				.taskRequest(taskRequestApi)
				.schedules(List.of(scheduledTaskScheduleRequestDto))
				.build();

			// Act
			ScheduledTask result = scheduledTaskService.updateScheduledTask(1, updateScheduledTaskDto);

			// Assert
			assertThat(result).isNotNull();
		}

		@Test
		@DisplayName("task_id에 해당하는 ScheduledTask가 없는 경우 NotFoundException을 반환한다.")
		void updateScheduledTaskNotFound() {
			UpdateScheduledTaskDto updateScheduledTaskDto = mock(UpdateScheduledTaskDto.class);
			when(scheduledTaskRepository.findById(anyInt())).thenReturn(Optional.empty());

			// Assert
			Assertions.assertThrows(NotFoundException.class,
				() -> scheduledTaskService.updateScheduledTask(1, updateScheduledTaskDto));
		}
	}

	@Nested
	@DisplayName("ClearScheduledTask")
	class ClearScheduledTask {
		@Test
		@DisplayName("task_id에 해당하는 스케쥴러의 Job을 삭제하고, exceptDates에 해당하는 날짜를 추가하여, 스케줄을 재등록한다.")
		void clearScheduledTask() throws SchedulerException {
			// Arrange
			List<String> exceptDates = new ArrayList<>();
			exceptDates.add("2024-04-22");
			ScheduledTask scheduledTask = ScheduledTask.builder()
				.exceptDates(exceptDates)
				.build();
			when(scheduledTaskRepository.findById(anyInt())).thenReturn(Optional.of(scheduledTask));
			doNothing().when(quartzService).scheduleTask(any(ScheduledTask.class));
			String isoDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

			// Act
			scheduledTaskService.clearScheduledTaskEvent(1, isoDateTime);

			// Assert
			assertThat(scheduledTask.getExceptDates()).isNotNull();
			assertThat(scheduledTask.getExceptDates().size()).isEqualTo(2);
		}
	}

	@Nested
	@DisplayName("DispatchScheduledTask")
	class DispatchScheduledTask {
		@Test
		@DisplayName("스케쥴러에 등록된 ScheduledTask를 실행하면 updateLastRun가 변경됩니다.")
		void dispatch() {
			// Arrange
			ScheduledTaskSchedule schedules = ScheduledTaskSchedule.builder().build();
			TaskRequestApi taskRequest = TaskRequestApi.builder().build();
			ScheduledTask scheduledTask = ScheduledTask.builder()
				.id(1)
				.taskRequest(taskRequest)
				.schedules(List.of(schedules))
				.build();
			when(scheduledTaskRepository.findById(anyInt())).thenReturn(Optional.of(scheduledTask));
			doNothing().when(taskService).postDispatchTask(any(TaskRequestApi.class));

			// Act
			scheduledTaskService.dispatchScheduledTask(1);

			// Assert
			assertThat(scheduledTask.getLastRan()).isNotNull();
		}

		@Test
		@DisplayName("스케쥴러에 등록된 ScheduledTask를 실행해도 exceptDates에 해당하는 경우 updateLastRun이 변경되지 않습니다.")
		void dispatchExceptDate() {
			ScheduledTaskSchedule schedules = ScheduledTaskSchedule.builder().build();
			TaskRequestApi taskRequest = TaskRequestApi.builder().build();
			ScheduledTask scheduledTask = ScheduledTask.builder()
				.id(1)
				.taskRequest(taskRequest)
				.schedules(List.of(schedules))
				.exceptDates(List.of(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)))
				.build();
			when(scheduledTaskRepository.findById(anyInt())).thenReturn(Optional.of(scheduledTask));

			// Act
			scheduledTaskService.dispatchScheduledTask(1);

			// Assert
			assertThat(scheduledTask.getLastRan()).isNull();
		}
	}
}
