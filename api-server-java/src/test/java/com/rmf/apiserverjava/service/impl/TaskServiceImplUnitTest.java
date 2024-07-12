package com.rmf.apiserverjava.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.dto.tasks.TaskLogRequestDto;
import com.rmf.apiserverjava.dto.tasks.TaskStatesQueryRequestDto;
import com.rmf.apiserverjava.dto.time.TimeRangeDto;
import com.rmf.apiserverjava.entity.tasks.TaskEventLog;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhases;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhasesEvents;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhasesEventsLog;
import com.rmf.apiserverjava.entity.tasks.TaskRequest;
import com.rmf.apiserverjava.entity.tasks.TaskState;
import com.rmf.apiserverjava.global.parser.LogBetweenParser;
import com.rmf.apiserverjava.repository.TaskEventLogLogRepository;
import com.rmf.apiserverjava.repository.TaskEventLogPhasesEventsLogRepository;
import com.rmf.apiserverjava.repository.TaskEventLogPhasesEventsRepository;
import com.rmf.apiserverjava.repository.TaskEventLogPhasesLogRepository;
import com.rmf.apiserverjava.repository.TaskEventLogPhasesRepository;
import com.rmf.apiserverjava.repository.TaskEventLogRepository;
import com.rmf.apiserverjava.repository.TaskRequestRepository;
import com.rmf.apiserverjava.repository.TaskStateQueryRepository;
import com.rmf.apiserverjava.repository.TaskStateRepository;
import com.rmf.apiserverjava.rmfapi.fleet.LogEntryApi;
import com.rmf.apiserverjava.rmfapi.tasks.PhasesApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskEventLogApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskRequestApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskStateApi;
import com.rmf.apiserverjava.rxjava.eventbus.TaskEvents;

import io.reactivex.rxjava3.subjects.PublishSubject;

@UnitTest
class TaskServiceImplUnitTest {

	@Mock
	TaskRequestRepository taskRequestRepository;

	@Mock
	TaskStateRepository taskStateRepository;

	@Mock
	TaskEventLogRepository logRepository;

	@Mock
	TaskEventLogLogRepository logLogRepository;

	@Mock
	TaskEventLogPhasesRepository logPhasesRepository;

	@Mock
	TaskEventLogPhasesLogRepository logPhasesLogRepository;

	@Mock
	TaskEventLogPhasesEventsRepository logPhasesEventsRepository;

	@Mock
	TaskEventLogPhasesEventsLogRepository logPhasesEventsLogRepository;

	@Mock
	TaskStateQueryRepository taskStateQueryRepository;

	@Mock
	LogBetweenParser logBetweenParser;

	@Mock
	TaskEvents taskEvents;

	@InjectMocks
	TaskServiceImpl taskServiceImpl;

	@Nested
	@DisplayName("getTaskRequest")
	class GetTaskRequest {
		@Test
		@DisplayName("조회에 성공할 경우 Optional<TaskRequestApi>를 반환한다.")
		void getTaskRequestFound() {
			//Arrange
			String id = "t1";
			TaskRequest taskRequest = mock(TaskRequest.class);
			TaskRequestApi requestApi = mock(TaskRequestApi.class);
			when(taskRequest.getRequest()).thenReturn(requestApi);
			when(taskRequestRepository.findById(eq(id))).thenReturn(Optional.ofNullable(taskRequest));

			//Act
			Optional<TaskRequestApi> result = taskServiceImpl.getTaskRequest(id);

			//Assert
			assertThat(result.get()).isEqualTo(requestApi);
		}

		@Test
		@DisplayName("조회에 실패할 경우 Optional.empty()를 반환한다.")
		void getTaskRequestNotFound() {
			//Arrange
			String id = "t1";
			when(taskRequestRepository.findById(eq(id))).thenReturn(Optional.empty());

			//Act
			Optional<TaskRequestApi> result = taskServiceImpl.getTaskRequest(id);

			//Assert
			assertThat(result).isEqualTo(Optional.empty());
		}
	}

	@Nested
	@DisplayName("queryTaskState")
	class QueryTaskState {

		@Test
		@DisplayName("조회에 성공할 경우 List<TaskStateApi>를 반환한다.")
		void queryTaskStateFound() {
			//Arrange
			TaskState state = mock(TaskState.class);
			TaskStateApi stateApi = mock(TaskStateApi.class);
			when(state.getData()).thenReturn(stateApi);
			when(taskStateQueryRepository.findAllTaskStateByQuery(any(), any())).thenReturn(List.of(state));

			//Act
			List<TaskStateApi> result = taskServiceImpl.getTaskStateList(mock(TaskStatesQueryRequestDto.class));

			//Assert
			assertThat(result).contains(stateApi);
		}

		@Test
		@DisplayName("쿼리가 존재할 경우, 쿼리에 맞는 TaskStateApi를 반환한다.")
		void queryTaskStateFoundWithQuery() {

			//Arrange
			TaskState state = mock(TaskState.class);
			TaskStateApi stateApi = mock(TaskStateApi.class);
			TaskStatesQueryRequestDto queryRequestDto = mock(TaskStatesQueryRequestDto.class);
			when(queryRequestDto.getTaskId()).thenReturn("t1");
			when(queryRequestDto.getCategory()).thenReturn("category");
			when(queryRequestDto.getStartTimeBetween()).thenReturn("0,10000");
			when(queryRequestDto.getFinishTimeBetween()).thenReturn("0,10000");
			when(queryRequestDto.getAssignedTo()).thenReturn("assignedTo");
			when(queryRequestDto.getStatus()).thenReturn("status");
			when(state.getData()).thenReturn(stateApi);
			when(taskStateQueryRepository.findAllTaskStateByQuery(any(), any())).thenReturn(List.of(state));

			//Act
			List<TaskStateApi> result = taskServiceImpl.getTaskStateList(queryRequestDto);

			//Assert
			assertThat(result).contains(stateApi);
		}

		@Test
		@DisplayName("조회에 실패할 경우 빈 List를 반환한다.")
		void queryTaskStateNotFound() {
			//Arrange
			when(taskStateQueryRepository.findAllTaskStateByQuery(any(), any())).thenReturn(List.of());

			//Act
			List<TaskStateApi> result = taskServiceImpl.getTaskStateList(mock(TaskStatesQueryRequestDto.class));

			//Assert
			assertThat(result).isEmpty();

		}
	}

	@Nested
	@DisplayName("getTaskState")
	class GetTaskState {
		@Test
		@DisplayName("조회에 성공할 경우 Optional<TaskStateApi>를 반환한다.")
		void getTaskStateFound() {
			//Arrange
			String id = "t1";
			TaskState state = mock(TaskState.class);
			TaskStateApi stateApi = mock(TaskStateApi.class);
			when(state.getData()).thenReturn(stateApi);
			when(taskStateRepository.findById(eq(id))).thenReturn(Optional.ofNullable(state));

			//Act
			Optional<TaskStateApi> result = taskServiceImpl.getTaskState(id);

			//Assert
			assertThat(result.get()).isEqualTo(stateApi);
		}

		@Test
		@DisplayName("조회에 실패할 경우 Optional.empty()를 반환한다.")
		void getTaskStateNotFound() {
			//Arrange
			String id = "t1";
			when(taskStateRepository.findById(eq(id))).thenReturn(Optional.empty());

			//Act
			Optional<TaskStateApi> result = taskServiceImpl.getTaskState(id);

			//Assert
			assertThat(result).isEqualTo(Optional.empty());
		}
	}

	@Nested
	@DisplayName("getTaskLog")
	class GetTaskLog {
		@Test
		@DisplayName("조회에 성공할 경우 Optional<TaskEventLogApi>를 반환한다.")
		void getTaskLogFound() {
			//Arrange
			String id = "t1";
			TaskLogRequestDto logRequestDto = mock(TaskLogRequestDto.class);
			when(logRequestDto.getTaskId()).thenReturn(id);
			when(logRequestDto.getTimeRange()).thenReturn(mock(TimeRangeDto.class));
			when(logRepository.findByIdFetchPhases(eq(id))).thenReturn(Optional.of(mock(TaskEventLog.class)));
			List<TaskEventLogPhases> logPhases = new ArrayList<>();
			TaskEventLogPhases phases = mock(TaskEventLogPhases.class);
			logPhases.add(phases);
			when(logPhasesRepository.findByIdFetchEvents(any())).thenReturn(logPhases);
			when(logPhasesLogRepository.findLogPhasesLogByLogPhasesIdAndTimeRangeFetch(anyInt(), anyLong(), anyLong()))
				.thenReturn(new ArrayList<>());
			when(logPhasesEventsRepository
				.findLogPhasesEventsLogByLogPhasesIdAndTimeRangeFetch(anyInt(), anyLong(), anyLong()))
				.thenReturn(new ArrayList<>());
			//Act
			Optional<TaskEventLogApi> result = taskServiceImpl.getTaskLog(logRequestDto);

			//Assert
			assertThat(result).isNotEmpty();
		}

		@Test
		@DisplayName("조회에 실패할 경우 Optional.empty()를 반환한다.")
		void getTaskLogNotFound() {
			//Arrange
			String id = "t1";
			TaskLogRequestDto logRequestDto = mock(TaskLogRequestDto.class);
			when(logRequestDto.getTaskId()).thenReturn(id);
			when(logRequestDto.getTimeRange()).thenReturn(mock(TimeRangeDto.class));

			//Act
			Optional<TaskEventLogApi> result = taskServiceImpl.getTaskLog(logRequestDto);

			//Assert
			assertThat(result).isEmpty();
		}
	}

	@Nested
	@DisplayName("updateOrCreateTaskState")
	class UpdateOrCreateTaskState {
		@Test
		@DisplayName("booking에 id에 해당하는 taskState가 존재하면 업데이트한다")
		void updateOrCreateTaskStateFound() {
			//Arrange
			TaskStateApi taskStateApi = mock(TaskStateApi.class);
			when(taskStateApi.getBooking()).thenReturn(mock(TaskStateApi.Booking.class));
			when(taskStateApi.getBooking().getId()).thenReturn("t1");
			TaskState taskState = mock(TaskState.class);
			when(taskStateRepository.findById(eq("t1"))).thenReturn(Optional.of(taskState));

			//Act
			taskServiceImpl.updateOrCreateTaskState(taskStateApi);

			//Assert
			verify(taskStateRepository, never()).save(any());
		}

		@Test
		@DisplayName("booking에 id에 해당하는 taskState가 존재하지 않으면 저장한다")
		void updateOrCreateTaskStateNotFound() throws InstantiationException, IllegalAccessException {
			//Arrange
			TaskStateApi taskStateApi = mock(TaskStateApi.class);
			when(taskStateApi.getBooking()).thenReturn(mock(TaskStateApi.Booking.class));
			when(taskStateApi.getBooking().getId()).thenReturn("t1");
			when(taskStateApi.getCategory()).thenReturn(mock(TaskStateApi.Category.class));
			when(taskStateApi.getAssignedTo()).thenReturn(mock(TaskStateApi.AssignedTo.class));
			when(taskStateApi.getStatus()).thenReturn(mock(TaskStateApi.Status.class));
			when(taskStateRepository.findById(eq("t1"))).thenReturn(Optional.empty());

			//Act
			taskServiceImpl.updateOrCreateTaskState(taskStateApi);

			//Assert
			verify(taskStateRepository).save(any());
		}
	}

	@Nested
	@DisplayName("saveTaskLog")
	class SaveTaskLog {
		@Test
		@DisplayName("taskEventLog를 조회하고 이미 존재하면 저장하지 않는다.")
		void saveTaskLogFound() {
			//Arrange
			TaskEventLogApi taskEventLogApi = mock(TaskEventLogApi.class);
			when(taskEventLogApi.getTaskId()).thenReturn("t1");
			when(logRepository.findById(eq("t1"))).thenReturn(Optional.of(mock(TaskEventLog.class)));

			//Act
			taskServiceImpl.saveTaskLog(taskEventLogApi);

			//Assert
			verify(logRepository, never()).save(any());
		}

		@Test
		@DisplayName("taskEventLog를 조회하고 존재하지 않으면 생성하여 반환한다")
		void saveTaskLogNotFound() {
			//Arrange
			TaskEventLogApi taskEventLogApi = mock(TaskEventLogApi.class);
			when(taskEventLogApi.getTaskId()).thenReturn("t1");

			//Act
			taskServiceImpl.saveTaskLog(taskEventLogApi);

			//Assert
			verify(logRepository).save(any());
		}

		@Test
		@DisplayName("taskEventApi에 Log가 존재하면 저장한다")
		void saveTaskLogWithLog() {
			//Arrange
			TaskEventLogApi taskEventLogApi = mock(TaskEventLogApi.class);
			when(taskEventLogApi.getTaskId()).thenReturn("t1");
			when(logRepository.findById(eq("t1"))).thenReturn(Optional.empty());
			when(taskEventLogApi.getLog()).thenReturn(new ArrayList<>());

			//api -> phase 가 존재하지 않음
			when(taskEventLogApi.getPhases()).thenReturn(null);

			//Act
			taskServiceImpl.saveTaskLog(taskEventLogApi);

			//Assert
			verify(logLogRepository).saveAll(any());
		}

		@Nested
		@DisplayName("TaskEventLogApi에 TaskEventLogPhases가 존재할 경우")
		class TaskEventLogApiWithPhases {

			@Test
			@DisplayName("phaseId로 taskEventLogPhase를 조회하고 이미 존재하면 저장하지 않는다.")
			void saveTaskLogWithPhasesFound() {
				//Arrange
				TaskEventLogApi taskEventLogApi = mock(TaskEventLogApi.class);
				when(taskEventLogApi.getTaskId()).thenReturn("t1");
				when(logRepository.findById(any())).thenReturn(Optional.of(mock(TaskEventLog.class)));
				when(taskEventLogApi.getLog()).thenReturn(null);

				//api -> phase가 존재함
				PhasesApi phase = mock(PhasesApi.class);
				Map<String, PhasesApi> phases = new HashMap<>();
				phases.put("p1", phase);
				when(taskEventLogApi.getPhases()).thenReturn(phases);

				//db -> phase가 존재함
				List<TaskEventLogPhases> list = new ArrayList<>();
				TaskEventLogPhases taskEventLogPhases = mock(TaskEventLogPhases.class);
				list.add(taskEventLogPhases);
				when(logPhasesRepository.findAllByTaskAndPhase(any(), eq("p1"))).thenReturn(list);

				//Act
				taskServiceImpl.saveTaskLog(taskEventLogApi);

				//Assert
				verify(logPhasesRepository, never()).save(any());
			}

			@Test
			@DisplayName("phaseId로 taskEventLogPhase를 조회하고 존재하지 않으면 생성하여 반환한다")
			void saveTaskLogWithPhasesNotFound() {
				//Arrange
				TaskEventLogApi taskEventLogApi = mock(TaskEventLogApi.class);
				when(taskEventLogApi.getTaskId()).thenReturn("t1");
				when(logRepository.findById(any())).thenReturn(Optional.of(mock(TaskEventLog.class)));
				when(taskEventLogApi.getLog()).thenReturn(null);

				//api -> phase가 존재함.
				PhasesApi phase = mock(PhasesApi.class);
				Map<String, PhasesApi> phases = new HashMap<>();
				phases.put("p1", phase);
				when(taskEventLogApi.getPhases()).thenReturn(phases);

				//db -> phase가 존재하지 않음.
				when(logPhasesRepository.findAllByTaskAndPhase(any(), eq("p1"))).thenReturn(new ArrayList<>());

				//Act
				taskServiceImpl.saveTaskLog(taskEventLogApi);

				//Assert
				verify(logPhasesRepository).save(any());
			}

			@Test
			@DisplayName("TaskEventLogPhasesLog가 존재하면 저장한다")
			void saveTaskLogWithPhasesLog() {
				//Arrange
				TaskEventLogApi taskEventLogApi = mock(TaskEventLogApi.class);
				when(taskEventLogApi.getTaskId()).thenReturn("t1");
				when(logRepository.findById(any())).thenReturn(Optional.of(mock(TaskEventLog.class)));
				when(taskEventLogApi.getLog()).thenReturn(null);

				//api -> phase가 존재함.
				PhasesApi phase = mock(PhasesApi.class);
				Map<String, PhasesApi> phases = new HashMap<>();
				phases.put("p1", phase);
				when(taskEventLogApi.getPhases()).thenReturn(phases);

				//api -> phase -> log가 존재함.
				List<LogEntryApi> logEntryApis = new ArrayList<>();
				logEntryApis.add(mock(LogEntryApi.class));
				when(phase.getLog()).thenReturn(logEntryApis);

				//api -> phase -> event가 존재하지 않음
				when(phase.getEvents()).thenReturn(null);

				//Act
				taskServiceImpl.saveTaskLog(taskEventLogApi);

				//Assert
				verify(logPhasesLogRepository).saveAll(any());
			}

			@Nested
			@DisplayName("TaskEventLogPhases에 TaskEventLogPhasesEvents가 존재할 경우")
			class TaskEventLogPhasesWithEvents {

				@Test
				@DisplayName("eventId로 TaskEventLogPhasesEvent를 조회하고 이미 존재하면 저장하지 않는다.")
				void saveTaskLogWithPhasesEventsFound() {
					//Assert
					TaskEventLogApi taskEventLogApi = mock(TaskEventLogApi.class);
					when(taskEventLogApi.getTaskId()).thenReturn("t1");
					when(logRepository.findById(any())).thenReturn(Optional.of(mock(TaskEventLog.class)));

					//api -> log가 존재하지 않음.
					when(taskEventLogApi.getLog()).thenReturn(null);

					//api -> phase가 존재함.
					PhasesApi phase = mock(PhasesApi.class);
					Map<String, PhasesApi> phases = new HashMap<>();
					phases.put("p1", phase);
					when(taskEventLogApi.getPhases()).thenReturn(phases);

					//api -> phase -> log가 존재하지 않음.
					when(phase.getLog()).thenReturn(null);

					//db -> phase가 존재함(저장 혹은 생성하므로 무조건 존재함)
					List<TaskEventLogPhases> list = new ArrayList<>();
					TaskEventLogPhases taskEventLogPhases = mock(TaskEventLogPhases.class);
					list.add(taskEventLogPhases);
					when(logPhasesRepository.findAllByTaskAndPhase(any(), eq("p1"))).thenReturn(list);

					//api -> phase -> event가 존재함.
					Map<String, List<LogEntryApi>> events = new HashMap<>();
					List<LogEntryApi> logEntryApis = new ArrayList<>();
					logEntryApis.add(mock(LogEntryApi.class));
					events.put("e1", logEntryApis);
					when(phase.getEvents()).thenReturn(events);

					//db -> event 안에 값이 하나 이상 존재함.
					when(logPhasesEventsRepository.findAllByPhasesAndEvent(anyInt(), eq("e1")))
						.thenReturn(List.of(mock(TaskEventLogPhasesEvents.class)));

					//Act
					taskServiceImpl.saveTaskLog(taskEventLogApi);

					//Assert
					verify(logPhasesEventsRepository, never()).save(any());
				}

				@Test
				@DisplayName("eventId로 TaskEventLogPhasesEvent를 조회하고 존재하지 않으면 생성하여 반환한다")
				void saveTaskLogWithPhasesEventsNotFound() {
					//Assert
					TaskEventLogApi taskEventLogApi = mock(TaskEventLogApi.class);
					when(taskEventLogApi.getTaskId()).thenReturn("t1");
					when(logRepository.findById(any())).thenReturn(Optional.of(mock(TaskEventLog.class)));

					//api -> log가 존재하지 않음.
					when(taskEventLogApi.getLog()).thenReturn(null);

					//api -> phase가 존재함.
					PhasesApi phase = mock(PhasesApi.class);
					Map<String, PhasesApi> phases = new HashMap<>();
					phases.put("p1", phase);
					when(taskEventLogApi.getPhases()).thenReturn(phases);

					//db -> phase가 존재함(저장 혹은 생성하므로 무조건 존재함)
					List<TaskEventLogPhases> list = new ArrayList<>();
					TaskEventLogPhases taskEventLogPhases = mock(TaskEventLogPhases.class);
					list.add(taskEventLogPhases);
					when(logPhasesRepository.findAllByTaskAndPhase(any(), eq("p1"))).thenReturn(list);

					//api -> phase -> log가 존재하지 않음.
					when(phase.getLog()).thenReturn(null);

					//api -> phase -> event가 존재함.
					Map<String, List<LogEntryApi>> events = new HashMap<>();
					List<LogEntryApi> logEntryApis = new ArrayList<>();
					logEntryApis.add(mock(LogEntryApi.class));
					events.put("e1", logEntryApis);
					when(phase.getEvents()).thenReturn(events);

					//db -> event 안에 값이 존재하지 않음.
					when(logPhasesEventsRepository.findAllByPhasesAndEvent(anyInt(), eq("e1")))
						.thenReturn(new ArrayList<>());

					//Act
					taskServiceImpl.saveTaskLog(taskEventLogApi);

					//Assert
					verify(logPhasesEventsRepository).save(any());
				}

				@Test
				@DisplayName("TaskEventLogPhasesEventsLog가 존재하면 저장한다")
				void saveTaskLogWithPhasesEventsLog() {
					//Assert
					TaskEventLogApi taskEventLogApi = mock(TaskEventLogApi.class);
					when(taskEventLogApi.getTaskId()).thenReturn("t1");
					when(logRepository.findById(any())).thenReturn(Optional.of(mock(TaskEventLog.class)));

					//api -> log가 존재하지 않음.
					when(taskEventLogApi.getLog()).thenReturn(null);

					//api -> phase가 존재함.
					PhasesApi phase = mock(PhasesApi.class);
					Map<String, PhasesApi> phases = new HashMap<>();
					phases.put("p1", phase);
					when(taskEventLogApi.getPhases()).thenReturn(phases);

					//db -> phase가 존재함(저장 혹은 생성하므로 무조건 존재함)
					List<TaskEventLogPhases> list = new ArrayList<>();
					TaskEventLogPhases taskEventLogPhases = mock(TaskEventLogPhases.class);
					list.add(taskEventLogPhases);
					when(logPhasesRepository.findAllByTaskAndPhase(any(), eq("p1"))).thenReturn(list);

					//api -> phase -> event가 존재함.
					Map<String, List<LogEntryApi>> events = new HashMap<>();
					List<LogEntryApi> eventsLogList = new ArrayList<>();
					LogEntryApi eventLogApi = mock(LogEntryApi.class);
					eventsLogList.add(eventLogApi);
					events.put("e1", eventsLogList);
					when(phase.getEvents()).thenReturn(events);

					//db -> event 안에 값이 존재하지 않음.
					when(logPhasesEventsRepository.findAllByPhasesAndEvent(anyInt(), eq("e1")))
						.thenReturn(new ArrayList<>());

					//Act
					taskServiceImpl.saveTaskLog(taskEventLogApi);

					//Assert
					verify(logPhasesEventsLogRepository).saveAll(any());
				}
			}
		}
	}

	@Nested
	@DisplayName("saveLogAcknowledgedTaskCompletion")
	class SaveLogAcknowledgedTaskCompletion {
		@Test
		@DisplayName("기존 TaskLog 및 TaskState가 존재할 경우, phase 데이터를 추가하여야 한다")
		void phaseInsert() {
			//Arrange
			String taskId = "t1";
			String acknowledgedBy = "user";
			long ackTimeMills = System.currentTimeMillis();

			TaskEventLog taskLog = mock(TaskEventLog.class);
			List<TaskEventLogPhases> phases = new ArrayList<>();
			when(taskLog.getPhases()).thenReturn(phases);
			when(logRepository.findByIdFetchPhases(eq(taskId))).thenReturn(Optional.of(taskLog));
			when(logPhasesRepository.save(any())).thenReturn(mock(TaskEventLogPhases.class));
			when(logPhasesEventsRepository.save(any())).thenReturn(mock(TaskEventLogPhasesEvents.class));
			when(logPhasesEventsLogRepository.save(any())).thenReturn(mock(TaskEventLogPhasesEventsLog.class));

			TaskState taskState = mock(TaskState.class);
			TaskStateApi taskStateApi = TaskStateApi.builder().build();
			when(taskStateRepository.findById(eq(taskId))).thenReturn(Optional.of(taskState));
			when(taskState.getData()).thenReturn(taskStateApi);

			PublishSubject taskStateSubject = mock(PublishSubject.class);
			when(taskEvents.getTaskStatesEvent()).thenReturn(taskStateSubject);

			//Act
			taskServiceImpl.saveLogAcknowledgedTaskCompletion(taskId, acknowledgedBy, ackTimeMills);

			//Assert
			verify(logPhasesRepository).save(any());
			verify(logPhasesEventsRepository).save(any());
			verify(logPhasesEventsLogRepository).save(any());
			verify(taskStateSubject).onNext(taskStateApi);
			assertThat(taskStateApi.getPhases()).size().isEqualTo(1);
		}

		@Test
		@DisplayName("기존 TaskLog 및 TaskState가 존재하지 않을 경우 바로 리턴한다")
		void taskNotExist() {
			//Arrange
			String taskId = "t1";
			String acknowledgedBy = "user";
			long ackTimeMills = System.currentTimeMillis();

			//Act
			taskServiceImpl.saveLogAcknowledgedTaskCompletion(taskId, acknowledgedBy, ackTimeMills);

			//Assert
			verify(logPhasesRepository, times(0)).save(any());
			verify(logPhasesEventsRepository, times(0)).save(any());
			verify(logPhasesEventsLogRepository, times(0)).save(any());
		}

		@Test
		@DisplayName("기존 TaskLog만 존재할 경우 TastLog에, phase 데이터를 추가하여야 한다")
		void onlyTaskLog() {
			//Arrange
			String taskId = "t1";
			String acknowledgedBy = "user";
			long ackTimeMills = System.currentTimeMillis();

			TaskEventLog taskLog = mock(TaskEventLog.class);
			List<TaskEventLogPhases> phases = new ArrayList<>();
			when(taskLog.getPhases()).thenReturn(phases);
			when(logRepository.findByIdFetchPhases(eq(taskId))).thenReturn(Optional.of(taskLog));
			when(logPhasesRepository.save(any())).thenReturn(mock(TaskEventLogPhases.class));
			when(logPhasesEventsRepository.save(any())).thenReturn(mock(TaskEventLogPhasesEvents.class));
			when(logPhasesEventsLogRepository.save(any())).thenReturn(mock(TaskEventLogPhasesEventsLog.class));

			PublishSubject taskStateSubject = mock(PublishSubject.class);

			//Act
			taskServiceImpl.saveLogAcknowledgedTaskCompletion(taskId, acknowledgedBy, ackTimeMills);

			//Assert
			verify(logPhasesRepository).save(any());
			verify(logPhasesEventsRepository).save(any());
			verify(logPhasesEventsLogRepository).save(any());
			verify(taskStateSubject, times(0)).onNext(any());
		}

		@Test
		@DisplayName("기존 TaskState만 존재할 경우 TaskState에 phase 데이터를 추가하여야 한다")
		void onlyTaskState() {
			//Arrange
			String taskId = "t1";
			String acknowledgedBy = "user";
			long ackTimeMills = System.currentTimeMillis();

			TaskState taskState = mock(TaskState.class);
			TaskStateApi taskStateApi = TaskStateApi.builder().build();
			when(taskStateRepository.findById(eq(taskId))).thenReturn(Optional.of(taskState));
			when(taskState.getData()).thenReturn(taskStateApi);

			PublishSubject taskStateSubject = mock(PublishSubject.class);
			when(taskEvents.getTaskStatesEvent()).thenReturn(taskStateSubject);

			//Act
			taskServiceImpl.saveLogAcknowledgedTaskCompletion(taskId, acknowledgedBy, ackTimeMills);

			//Assert
			verify(logPhasesRepository, times(0)).save(any());
			verify(logPhasesEventsRepository, times(0)).save(any());
			verify(logPhasesEventsLogRepository, times(0)).save(any());
			verify(taskStateSubject).onNext(taskStateApi);
			assertThat(taskStateApi.getPhases()).size().isEqualTo(1);
		}
	}

	@Test
	@DisplayName("postDispatchTask 호출 테스트")
	void postDispatchTask() {
		//Arrange
		TaskRequestApi api = mock(TaskRequestApi.class);

		//Act
		//Assert
		taskServiceImpl.postDispatchTask(api);
	}
}
