package com.rmf.apiserverjava.entity.tasks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.rmf.apiserverjava.config.annotation.UnitTest;

@UnitTest
class TaskEventLogLogUnitTest {

	@Nested
	@DisplayName("TaskEventLogLog")
	class TaskRequestTest {

		@Nested
		@DisplayName("TaskEventLogLog constructor")
		class Constructor {
			@Test
			@DisplayName("TaskEventLogLog 객체 생성에 성공한다")
			void createTaskRequest() {
				// Arrange
				String id = "t1";

				// Act
				TaskEventLogLog taskEventLogLog = TaskEventLogLog.builder().build();
				// Assert
				assertNotNull(taskEventLogLog);
			}
		}

		@Nested
		@DisplayName("setTaskEventLogWithoutRelation")
		class SetTaskEventLogWithoutRelation {
			@Test
			@DisplayName("TaskEventLogLog에서 연관관계 동기화를 하지 않고 taskLog를 설정한다")
			void setTaskEventLogWithoutRelation() {
				// Arrange
				TaskEventLog taskEventLog = mock(TaskEventLog.class);
				TaskEventLogLog taskEventLogLog = TaskEventLogLog.builder().build();

				// Act
				taskEventLogLog.setTaskEventLogWithoutRelation(taskEventLog);
				// Assert
				assertNotNull(taskEventLogLog);
			}
		}
	}
}
