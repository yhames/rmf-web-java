package com.rmf.apiserverjava.entity.tasks;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.rmf.apiserverjava.config.annotation.UnitTest;

@UnitTest
class TaskEventLogUnitTest {

	@Nested
	@DisplayName("TaskEventLog")
	class TaskRequestTest {

		@Nested
		@DisplayName("TaskEventLog constructor")
		class Constructor {
			@Test
			@DisplayName("TaskEventLog 객체 생성에 성공한다")
			void createTaskRequest() {
				// Arrange
				String id = "t1";

				// Act
				TaskEventLog taskEventLog = new TaskEventLog(id);
				// Assert
				assertNotNull(taskEventLog);
			}
		}
	}
}
