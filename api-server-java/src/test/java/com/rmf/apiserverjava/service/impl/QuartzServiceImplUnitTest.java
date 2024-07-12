package com.rmf.apiserverjava.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTask;
import com.rmf.apiserverjava.global.exception.custom.InvalidClientArgumentException;

@UnitTest
class QuartzServiceImplUnitTest {

	@Mock
	SchedulerFactoryBean schedulerFactoryBean;

	@InjectMocks
	QuartzServiceImpl quartzService;

	private final SchedulerFactory schedulerFactory = new StdSchedulerFactory();

	@Nested
	@DisplayName("scheduleTask")
	class ScheduleTask {
		@Test
		@DisplayName("List<Trigger>를 생성하고 Job을 스케줄링합니다.")
		void addJob() throws SchedulerException {
			// Arrange
			ScheduledTask scheduledTask = mock(ScheduledTask.class);
			Trigger trigger = TriggerBuilder.newTrigger()
				.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(1))
				.build();
			when(schedulerFactoryBean.getScheduler()).thenReturn(schedulerFactory.getScheduler());
			when(scheduledTask.getTriggers()).thenReturn(Set.of(trigger));

			// Act
			// Assert
			quartzService.scheduleTask(scheduledTask);
		}

		@Test
		@DisplayName("List<Trigger>가 비어있는 ScheduledTask를 스케줄링하면 예외를 던집니다.")
		void addJobWithNoTrigger() {
			// Arrange
			ScheduledTask scheduledTask = mock(ScheduledTask.class);
			when(scheduledTask.getTriggers()).thenReturn(Set.of());

			// Act
			// Assert
			Assertions.assertThrows(InvalidClientArgumentException.class,
				() -> quartzService.scheduleTask(scheduledTask));
		}
	}

	@Nested
	@DisplayName("unScheduleTask")
	class UnScheduleTask {
		@Test
		@DisplayName("ScheduledTask를 스케줄러에서 제거합니다.")
		void removeJob() throws SchedulerException {
			// Arrange
			ScheduledTask scheduledTask = mock(ScheduledTask.class);
			Trigger trigger = TriggerBuilder.newTrigger()
				.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(1))
				.build();
			when(schedulerFactoryBean.getScheduler()).thenReturn(schedulerFactory.getScheduler());
			when(scheduledTask.getTriggers()).thenReturn(Set.of(trigger));
			quartzService.scheduleTask(scheduledTask);

			// Act
			// Assert
			quartzService.unScheduleTask(scheduledTask);
			assertThat(quartzService.getScheduledTaskJobCount()).isEqualTo(0);
		}

		@Test
		@DisplayName("등록되지 않은 ScheduledTask를 제거하는 함수를 호출합니다.")
		void removeJobNotFound() throws SchedulerException {
			// Arrange
			ScheduledTask scheduledTask = mock(ScheduledTask.class);
			when(schedulerFactoryBean.getScheduler()).thenReturn(schedulerFactory.getScheduler());

			// Act
			// Assert
			quartzService.unScheduleTask(scheduledTask);
			assertThat(quartzService.getScheduledTaskJobCount()).isEqualTo(0);
		}
	}
}
