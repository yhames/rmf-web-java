package com.rmf.apiserverjava.entity.tasks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.rmf.apiserverjava.config.annotation.UnitTest;

@UnitTest
class TaskEventLogPhasesEventsUnitTest {

	@Nested
	@DisplayName("TaskEventLogPhasesEvents")
	class TaskRequestTest {

		@Nested
		@DisplayName("TaskEventLogPhasesEvents constructor")
		class Constructor {

			@Test
			@DisplayName("TaskEventLogPhasesEvents 객체 생성에 성공한다")
			void createTaskRequest() {
				// Arrange

				// Act
				TaskEventLogPhasesEvents taskEventLogPhasesEvents = TaskEventLogPhasesEvents.builder().build();
				// Assert
				assertNotNull(taskEventLogPhasesEvents);
			}
		}
	}
}
