package com.rmf.apiserverjava.entity.tasks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.rmfapi.tasks.TaskStateApi;

@UnitTest
class TaskStateUnitTest {

	@Nested
	@DisplayName("TaskState")
	class TaskRequestTest {

		@Nested
		@DisplayName("TaskState constructor")
		class Constructor {
			@Test
			@DisplayName("Builder 통해 TaskState 객체를 생성에 성공한다")
			void createTaskRequest() {
				// Arrange
				String id = "t1";
				TaskStateApi stateApi = mock(TaskStateApi.class);

				// Act
				TaskState taskState = TaskState.builder().id(id).data(stateApi).build();
				// Assert
				assertNotNull(taskState);
			}
		}

		@Nested
		@DisplayName("TaskState update")
		class Update {
			@Test
			@DisplayName("TaskState 객체를 업데이트에 성공한다")
			void updateTaskRequest() {
				// Arrange
				TaskStateApi stateApi = mock(TaskStateApi.class);
				when(stateApi.getCategory()).thenReturn(mock(TaskStateApi.Category.class));
				when(stateApi.getAssignedTo()).thenReturn(mock(TaskStateApi.AssignedTo.class));
				when(stateApi.getUnixMillisStartTime()).thenReturn(1L);
				when(stateApi.getUnixMillisFinishTime()).thenReturn(1L);
				when(stateApi.getStatus()).thenReturn(TaskStateApi.Status.completed);
				when(stateApi.getBooking()).thenReturn(mock(TaskStateApi.Booking.class));
				when(stateApi.getBooking().getUnixMillisRequestTime()).thenReturn(1L);
				when(stateApi.getBooking().getRequester()).thenReturn("requester");
				TaskState taskState = TaskState.builder().id("t1").data(stateApi).build();

				// Act
				taskState.updateTaskState(stateApi);
				// Assert
				assertNotNull(taskState);
			}
		}
	}
}
