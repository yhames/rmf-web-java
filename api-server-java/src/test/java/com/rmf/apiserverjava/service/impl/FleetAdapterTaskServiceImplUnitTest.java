package com.rmf.apiserverjava.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.entity.alerts.Alert;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogLog;
import com.rmf.apiserverjava.rmfapi.Tier;
import com.rmf.apiserverjava.rmfapi.fleet.LogEntryApi;
import com.rmf.apiserverjava.rmfapi.tasks.PhasesApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskEventLogApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskStateApi;
import com.rmf.apiserverjava.rxjava.eventbus.AlertEvents;
import com.rmf.apiserverjava.rxjava.eventbus.TaskEvents;
import com.rmf.apiserverjava.service.AlertService;
import com.rmf.apiserverjava.service.TaskService;

import io.reactivex.rxjava3.subjects.PublishSubject;

@UnitTest
class FleetAdapterTaskServiceImplUnitTest {

	@Mock
	private TaskService taskService;

	@Mock
	private TaskEvents taskEvents;

	@Mock
	private AlertEvents alertEvents;

	@Mock
	private AlertService alertService;

	@InjectMocks
	private FleetAdapterTaskServiceImpl fleetAdapterTaskServiceImpl;

	@Nested
	@DisplayName("saveOrUpdateTaskState")
	class SaveOrUpdateTaskState {

		@Test
		@DisplayName("TaskStateApi의 상태가 completed일 때 알림을 생성하고, 발행한다")
		void taskStateApiStatusIsCompleted() {
			//Arrange
			TaskStateApi taskStateApi = mock(TaskStateApi.class);
			when(taskStateApi.getStatus()).thenReturn(TaskStateApi.Status.completed);
			when(taskStateApi.getBooking()).thenReturn(mock(TaskStateApi.Booking.class));
			when(taskStateApi.getBooking().getId()).thenReturn("bookingId");
			PublishSubject taskStateEvent = mock(PublishSubject.class);
			when(taskEvents.getTaskStatesEvent()).thenReturn(taskStateEvent);
			when(alertEvents.getAlertsEvent()).thenReturn(mock(PublishSubject.class));
			when(alertService.createAlert(any())).thenReturn(Optional.of(mock(Alert.class)));

			//Act
			fleetAdapterTaskServiceImpl.saveOrUpdateTaskState(taskStateApi);

			//Assert
			verify(alertService).createAlert(any());
			verify(alertEvents.getAlertsEvent()).onNext(any());
		}

		@Test
		@DisplayName("알림생성에 실패했을 시, 알림 이벤트를 발행하지 않는다.")
		void alertServiceCreateAlertFailed() {
			//Arrange
			TaskStateApi taskStateApi = mock(TaskStateApi.class);
			when(taskStateApi.getStatus()).thenReturn(TaskStateApi.Status.completed);
			when(taskStateApi.getBooking()).thenReturn(mock(TaskStateApi.Booking.class));
			when(taskStateApi.getBooking().getId()).thenReturn("bookingId");
			PublishSubject taskStateEvent = mock(PublishSubject.class);
			when(taskEvents.getTaskStatesEvent()).thenReturn(taskStateEvent);
			when(alertEvents.getAlertsEvent()).thenReturn(mock(PublishSubject.class));
			when(alertService.createAlert(any())).thenReturn(Optional.empty());

			//Act
			fleetAdapterTaskServiceImpl.saveOrUpdateTaskState(taskStateApi);

			//Assert
			verify(alertEvents.getAlertsEvent(), never()).onNext(any());
		}

		@Test
		@DisplayName("TaskStateApi의 상태가 completed가 아닐 때 알림을 생성하지 않는다.")
		void taskStateApiStatusIsNotCompleted() {
			//Arrange
			TaskStateApi taskStateApi = mock(TaskStateApi.class);
			when(taskStateApi.getStatus()).thenReturn(TaskStateApi.Status.failed);
			when(taskEvents.getTaskStatesEvent()).thenReturn(mock(PublishSubject.class));
			when(alertEvents.getAlertsEvent()).thenReturn(mock(PublishSubject.class));

			//Act
			fleetAdapterTaskServiceImpl.saveOrUpdateTaskState(taskStateApi);

			//Assert
			verify(alertService, never()).createAlert(any());
			verify(alertEvents.getAlertsEvent(), never()).onNext(any());
		}
	}

	@Nested
	@DisplayName("saveTaskEventLog")
	class SaveTaskEventLog {

		@Test
		@DisplayName("TaskEventLogApi의 값으로 TaskService의 saveTaskLog를 저장 로직을 호출하고 관련 이벤트를 발행한다.")
		void taskEventLogApi() {
			//Arrange
			TaskEventLogApi logApi = mock(TaskEventLogApi.class);
			when(logApi.getLog()).thenReturn(null);
			when(logApi.getPhases()).thenReturn(null);
			PublishSubject taskStateEvent = mock(PublishSubject.class);
			when(taskEvents.getTaskEventLogsEvent()).thenReturn(taskStateEvent);

			//Act
			fleetAdapterTaskServiceImpl.saveTaskEventLog(logApi);

			//Assert
			verify(taskService).saveTaskLog(logApi);
			verify(taskStateEvent).onNext(logApi);
		}


		@Test
		@DisplayName("TaskEventLogApi에 에러가 없을 때 알림을 생성하지 않는다.")
		void taskEventLogApiHasNoError() {
			//Arrange
			TaskEventLogApi logApi = mock(TaskEventLogApi.class);
			LogEntryApi eventLog = mock(LogEntryApi.class);
			PhasesApi phasesApi = mock(PhasesApi.class);
			when(eventLog.getTier()).thenReturn(Tier.info);
			when(logApi.getPhases()).thenReturn(Map.of("phase", phasesApi));
			when(phasesApi.getEvents()).thenReturn(Map.of("event", List.of(eventLog)));
			PublishSubject taskStateEvent = mock(PublishSubject.class);
			when(taskEvents.getTaskEventLogsEvent()).thenReturn(taskStateEvent);
			when(alertEvents.getAlertsEvent()).thenReturn(mock(PublishSubject.class));

			//Act
			fleetAdapterTaskServiceImpl.saveTaskEventLog(logApi);

			//Assert
			verify(alertService, never()).createAlert(any());
			verify(alertEvents.getAlertsEvent(), never()).onNext(any());
		}

		@Test
		@DisplayName("TaskEventLogApi의 log에 에러가 있을 때 알림을 생성한다.")
		void taskEventLogApiLogHasError() {
			//Arrange
			TaskEventLogApi logApi = mock(TaskEventLogApi.class);
			LogEntryApi logLog = mock(LogEntryApi.class);
			when(logLog.getTier()).thenReturn(Tier.error);
			when(logApi.getLog()).thenReturn(List.of(logLog));
			PublishSubject taskStateEvent = mock(PublishSubject.class);
			when(taskEvents.getTaskEventLogsEvent()).thenReturn(taskStateEvent);
			when(alertEvents.getAlertsEvent()).thenReturn(mock(PublishSubject.class));
			when(alertService.createAlert(any())).thenReturn(Optional.of(mock(Alert.class)));

			//Act
			fleetAdapterTaskServiceImpl.saveTaskEventLog(logApi);

			//Assert
			verify(alertService).createAlert(any());
			verify(alertEvents.getAlertsEvent()).onNext(any());
		}

		@Test
		@DisplayName("TaskEventLogApi의 phases의 log에 에러가 있을 때 알림을 생성한다.")
		void taskEventLogApiPhasesLogHasError() {
			//Arrange
			TaskEventLogApi logApi = mock(TaskEventLogApi.class);
			LogEntryApi phaseLog = mock(LogEntryApi.class);
			PhasesApi phasesApi = mock(PhasesApi.class);
			when(logApi.getPhases()).thenReturn(Map.of("phase", phasesApi));
			when(phasesApi.getLog()).thenReturn(List.of(phaseLog));
			when(phaseLog.getTier()).thenReturn(Tier.error);
			PublishSubject taskStateEvent = mock(PublishSubject.class);
			when(taskEvents.getTaskEventLogsEvent()).thenReturn(taskStateEvent);
			when(alertEvents.getAlertsEvent()).thenReturn(mock(PublishSubject.class));
			when(alertService.createAlert(any())).thenReturn(Optional.of(mock(Alert.class)));

			//Act
			fleetAdapterTaskServiceImpl.saveTaskEventLog(logApi);

			//Assert
			verify(alertService).createAlert(any());
			verify(alertEvents.getAlertsEvent()).onNext(any());
		}

		@Test
		@DisplayName("TaskEventLogApi의 phases는 존재하나, log와 event가 존재하지 않을 때 알림을 생성하지 않는다.")
		void taskEventLogApiPhasesLogAndEventIsNull() {
			//Arrange
			TaskEventLogApi logApi = mock(TaskEventLogApi.class);
			PhasesApi phasesApi = mock(PhasesApi.class);
			when(logApi.getPhases()).thenReturn(Map.of("phase", phasesApi));
			when(phasesApi.getLog()).thenReturn(null);
			when(phasesApi.getEvents()).thenReturn(null);
			PublishSubject taskStateEvent = mock(PublishSubject.class);
			when(taskEvents.getTaskEventLogsEvent()).thenReturn(taskStateEvent);
			when(alertEvents.getAlertsEvent()).thenReturn(mock(PublishSubject.class));

			//Act
			fleetAdapterTaskServiceImpl.saveTaskEventLog(logApi);

			//Assert
			verify(alertService, never()).createAlert(any());
			verify(alertEvents.getAlertsEvent(), never()).onNext(any());
		}

		@Test
		@DisplayName("TaskEventLogApi의 phases의 events의 log에 에러가 있을 때 알림을 생성한다.")
		void taskEventLogApiPhasesEventsLogHasError() {
			//Arrange
			TaskEventLogApi logApi = mock(TaskEventLogApi.class);
			LogEntryApi eventLog = mock(LogEntryApi.class);
			PhasesApi phasesApi = mock(PhasesApi.class);
			when(eventLog.getTier()).thenReturn(Tier.error);
			when(logApi.getPhases()).thenReturn(Map.of("phase", phasesApi));
			when(phasesApi.getEvents()).thenReturn(Map.of("event", List.of(eventLog)));
			PublishSubject taskStateEvent = mock(PublishSubject.class);
			when(taskEvents.getTaskEventLogsEvent()).thenReturn(taskStateEvent);
			when(alertEvents.getAlertsEvent()).thenReturn(mock(PublishSubject.class));
			when(alertService.createAlert(any())).thenReturn(Optional.of(mock(Alert.class)));

			//Act
			fleetAdapterTaskServiceImpl.saveTaskEventLog(logApi);

			//Assert
			verify(alertService).createAlert(any());
			verify(alertEvents.getAlertsEvent()).onNext(any());
		}

		@Test
		@DisplayName("알림생성에 실패했을 시, 알림 이벤트를 발행하지 않는다.")
		void alertServiceCreateAlertFailed() {
			//Arrange
			TaskEventLogApi logApi = mock(TaskEventLogApi.class);
			LogEntryApi logLog = mock(LogEntryApi.class);
			when(logLog.getTier()).thenReturn(Tier.error);
			when(logApi.getLog()).thenReturn(List.of(logLog));
			PublishSubject taskStateEvent = mock(PublishSubject.class);
			when(taskEvents.getTaskEventLogsEvent()).thenReturn(taskStateEvent);
			when(alertEvents.getAlertsEvent()).thenReturn(mock(PublishSubject.class));
			when(alertService.createAlert(any())).thenReturn(Optional.empty());

			//Act
			fleetAdapterTaskServiceImpl.saveTaskEventLog(logApi);

			//Assert
			verify(alertEvents.getAlertsEvent(), never()).onNext(any());
		}
	}
}
