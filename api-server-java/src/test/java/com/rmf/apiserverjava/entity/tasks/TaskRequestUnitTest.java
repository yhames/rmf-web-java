package com.rmf.apiserverjava.entity.tasks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.rmfapi.tasks.TaskRequestApi;

@UnitTest
class TaskRequestUnitTest {

	@Nested
	@DisplayName("TaskRequest")
	class TaskRequestTest {

		@Nested
		@DisplayName("TaskRequest constructor")
		class Constructor {
			@Test
			@DisplayName("Builder 통해 TaskRequest 객체를 생성에 성공한다")
			void createTaskRequest() {
				// Arrange
				String id = "t1";
				TaskRequestApi requestApi = mock(TaskRequestApi.class);

				// Act
				TaskRequest taskRequest = TaskRequest.builder()
					.id(id).request(requestApi).build();
				// Assert
				assertNotNull(taskRequest);
			}
		}
	}
}
