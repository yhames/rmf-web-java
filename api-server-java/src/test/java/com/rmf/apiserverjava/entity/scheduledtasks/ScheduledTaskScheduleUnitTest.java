package com.rmf.apiserverjava.entity.scheduledtasks;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.quartz.Trigger;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTaskSchedule.Period;

@UnitTest
class ScheduledTaskScheduleUnitTest {

	@Nested
	@DisplayName("ScheduledTaskSchedule")
	class ScheduledTaskScheduleTest {
		@ParameterizedTest
		@ValueSource(strings = {"monday", "tuesday", "wednesday", "thursday", "friday",
			"saturday", "sunday", "day", "hour", "minute"})
		@DisplayName("ScheduledTaskSchedule을 Trigger로 변환합니다.")
		void toTrigger(String period) {
			//Arrange
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.id(1)
				.startFrom(timestamp)
				.until(timestamp)
				.every((short)1)
				.at("18:44")
				.period(Period.valueOf(period))
				.build();

			//Act
			Trigger result = scheduledTaskSchedule.toTrigger();
			LocalDateTime startTime = result.getStartTime()
				.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime()
				.withNano(0);
			LocalDateTime endTime = result.getEndTime()
				.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime()
				.withNano(0);

			//Assert
			Assertions.assertThat(startTime).isEqualTo(timestamp.toLocalDateTime().withNano(0));
			Assertions.assertThat(endTime).isEqualTo(timestamp.toLocalDateTime().withNano(0));
		}

		@ParameterizedTest
		@ValueSource(strings = {"monday", "tuesday", "wednesday", "thursday", "friday",
			"saturday", "sunday", "day", "hour", "minute"})
		@DisplayName("ScheduledTaskSchedule을 Trigger로 변환합니다.")
		void toTriggerWithNoEvery(String period) {
			//Arrange
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ScheduledTaskSchedule scheduledTaskSchedule = ScheduledTaskSchedule.builder()
				.id(1)
				.startFrom(timestamp)
				.until(timestamp)
				.at("18:44")
				.period(Period.valueOf(period))
				.build();

			//Act
			Trigger result = scheduledTaskSchedule.toTrigger();
			LocalDateTime startTime = result.getStartTime()
				.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime()
				.withNano(0);
			LocalDateTime endTime = result.getEndTime()
				.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime()
				.withNano(0);

			//Assert
			Assertions.assertThat(startTime).isEqualTo(timestamp.toLocalDateTime().withNano(0));
			Assertions.assertThat(endTime).isEqualTo(timestamp.toLocalDateTime().withNano(0));
		}
	}
}
