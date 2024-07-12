package com.rmf.apiserverjava.entity.scheduledtasks;

import static org.assertj.core.api.Assertions.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.quartz.Trigger;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTaskSchedule.Period;
import com.rmf.apiserverjava.rmfapi.tasks.TaskRequestApi;

@UnitTest
public class ScheduledTaskUnitTest {

	@Nested
	@DisplayName("ScheduledTask")
	class ScheduledTaskTest {
		@Test
		@DisplayName("ScheduledTask의 마지막 실행 시간을 변경합니다.")
		void updateLastRan() {
			// Arrange
			ScheduledTask scheduledTask = ScheduledTask.builder().build();
			LocalDateTime now = LocalDateTime.now();

			// Act
			scheduledTask.updateLastRun(now);

			// Assert
			assertThat(scheduledTask.getLastRan()).isEqualTo(Timestamp.valueOf(now));
		}

		@Test
		@DisplayName("ScheduledTask의 exceptDates를 추가합니다.")
		void exceptDates() {
			// Arrange
			ScheduledTask scheduledTask = ScheduledTask.builder().build();

			// Act
			String exceptDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
			scheduledTask.addExceptDates(exceptDate);
			scheduledTask.addExceptDates(exceptDate);
			scheduledTask.addExceptDates(exceptDate);

			// Assert
			assertThat(scheduledTask.getExceptDates()).isNotNull();
			assertThat(scheduledTask.getExceptDates().size()).isEqualTo(3);
		}

		@Test
		@DisplayName("ScheduledTask의 exceptDates가 null인 경우 동작하지 않습니다.")
		void exceptDatesResetWithNull() {
			// Arrange
			ScheduledTask scheduledTask = ScheduledTask.builder().build();

			// Act
			scheduledTask.resetExceptDates();

			// Assert
			assertThat(scheduledTask.getExceptDates()).isNotNull();
			assertThat(scheduledTask.getExceptDates().size()).isEqualTo(0);
		}

		@Test
		@DisplayName("ScheduledTask의 exceptDates를 초기화합니다.")
		void exceptDatesReset() {
			// Arrange
			List<String> exceptDates = new ArrayList<>();
			exceptDates.add(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));
			ScheduledTask scheduledTask = ScheduledTask.builder().exceptDates(exceptDates).build();

			// Act
			scheduledTask.resetExceptDates();

			// Assert
			assertThat(scheduledTask.getExceptDates()).isNotNull();
			assertThat(scheduledTask.getExceptDates().size()).isEqualTo(0);
		}

		@Test
		@DisplayName("ScheduledTask에서 Trigger를 가져옵니다.")
		void getTrigger() {
			// Arrange
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			List<ScheduledTaskSchedule> schedules = new ArrayList<>();
			int count = 10;
			for (int i = 0; i < count; i++) {
				schedules.add(ScheduledTaskSchedule.builder()
					.id(i).startFrom(timestamp).until(timestamp).at("18:44")
					.period(Period.valueOf("day")).build());
			}

			ScheduledTask scheduledTask = ScheduledTask.builder()
				.id(1)
				.taskRequest(TaskRequestApi.builder().build())
				.schedules(schedules)
				.build();

			// Act
			Set<Trigger> triggers = scheduledTask.getTriggers();

			// Assert
			assertThat(triggers.size()).isEqualTo(count);
		}

		@Test
		@DisplayName("ScheduledTask를 업데이트합니다.")
		void update() {
			// Arrange
			ScheduledTask scheduledTask = ScheduledTask.builder().build();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			List<ScheduledTaskSchedule> schedules = new ArrayList<>();
			int count = 10;
			for (int i = 0; i < count; i++) {
				schedules.add(ScheduledTaskSchedule.builder()
					.id(i).startFrom(timestamp).until(timestamp).at("18:44")
					.period(Period.valueOf("day")).build());
			}
			long time = System.currentTimeMillis();
			TaskRequestApi taskRequest = TaskRequestApi.builder().unixMillisRequestTime(time).build();

			// Act
			scheduledTask.update(taskRequest, schedules);

			// Assert
			assertThat(scheduledTask.getTaskRequest().getUnixMillisRequestTime()).isEqualTo(time);
			assertThat(scheduledTask.getSchedules().size()).isEqualTo(count);
		}
	}
}
