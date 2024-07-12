package com.rmf.apiserverjava.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.rmf.apiserverjava.config.annotation.IntegrationTest;
import com.rmf.apiserverjava.entity.alerts.Alert;
import com.rmf.apiserverjava.entity.tasks.TaskEventLog;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogLog;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhases;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhasesEvents;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhasesEventsLog;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhasesLog;
import com.rmf.apiserverjava.entity.tasks.TaskState;
import com.rmf.apiserverjava.repository.AlertRepository;
import com.rmf.apiserverjava.repository.TaskEventLogRepository;
import com.rmf.apiserverjava.repository.TaskStateRepository;
import com.rmf.apiserverjava.rmfapi.Tier;
import com.rmf.apiserverjava.rmfapi.tasks.PhasesApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskEventLogApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskStateApi;
import com.rmf.apiserverjava.service.impl.FleetAdapterTaskServiceImpl;

import jakarta.persistence.EntityManager;

@IntegrationTest
@Transactional
public class FleetAdapterTaskServiceImplIntegrationTest {

	@Autowired
	EntityManager em;

	@Autowired
	FleetAdapterTaskServiceImpl fleetAdapterTaskServiceImpl;

	@Autowired
	TaskStateRepository taskStateRepository;

	@Autowired
	AlertRepository alertRepository;

	@Autowired
	TaskEventLogRepository logRepository;

	@Nested
	@DisplayName("saveOrUpdateTaskState")
	class SaveOrUpdateTaskState {

		@Nested
		@DisplayName("TaskState 생성 혹은 업데이트")
		class CreateOrUpdateTaskState {

			@Test
			@DisplayName("id로 TaskState를 조회하고 존재하지 않을 경우 새로운 TaskState를 생성한다.")
			void createTaskState() {
				// Arrange
				TaskStateApi taskStateApi = TaskStateApi.builder()
					.category(new TaskStateApi.Category("category"))
					.assignedTo(TaskStateApi.AssignedTo.builder().name("name").build())
					.status(TaskStateApi.Status.blocked)
					.booking(new TaskStateApi.Booking("id",
						0L, 0L, 1, new ArrayList<>(), "requester"))
					.build();
				// Act
				fleetAdapterTaskServiceImpl.saveOrUpdateTaskState(taskStateApi);

				// Assert
				Optional<TaskState> taskStateOptional = taskStateRepository.findById("id");
				assertThat(taskStateOptional).isPresent();
			}

			@Test
			@DisplayName("id로 TaskState를 조회하고 존재할 경우 TaskState를 업데이트한다.")
			void updateTaskState() {
				// Arrange
				TaskStateApi taskStateApi = TaskStateApi.builder()
					.category(new TaskStateApi.Category("category"))
					.assignedTo(TaskStateApi.AssignedTo.builder().name("name").build())
					.status(TaskStateApi.Status.blocked)
					.booking(new TaskStateApi.Booking("id",
						0L, 0L, Map.of("Str", "obj"), new ArrayList<>(), "requester"))
					.build();
				em.persist(TaskState.builder().id("id").data(taskStateApi).build());

				// Act
				fleetAdapterTaskServiceImpl.saveOrUpdateTaskState(taskStateApi);

				// Assert
				List<TaskState> all = taskStateRepository.findAll();
				assertThat(all.size()).isEqualTo(1);
				assertThat(all.get(0).getData().getBooking().getPriority().toString()).contains("Str=obj");
			}
		}

		@Nested
		@DisplayName("Alert 생성 체크")
		class CheckCreateAlert {

			@Test
			@DisplayName("taskStateApi의 status가 'COMPLETE'일 경우 새로운 Alert를 생성한다")
			void createAlert() {
				// Arrange
				TaskStateApi taskStateApi = TaskStateApi.builder()
					.category(new TaskStateApi.Category("category"))
					.assignedTo(TaskStateApi.AssignedTo.builder().name("name").build())
					.status(TaskStateApi.Status.completed)
					.booking(new TaskStateApi.Booking("id",
						0L, 0L, 1, new ArrayList<>(), "requester"))
					.build();
				// Act
				fleetAdapterTaskServiceImpl.saveOrUpdateTaskState(taskStateApi);

				// Assert
				List<Alert> alerts = alertRepository.findAll();
				assertThat(alerts.size()).isEqualTo(1);
			}

			@Test
			@DisplayName("taskStateApi의 status가 'COMPLETE'가 아닐 경우 Alert를 생성하지 않는다.")
			void notCreateAlert() {
				// Arrange
				TaskStateApi taskStateApi = TaskStateApi.builder()
					.category(new TaskStateApi.Category("category"))
					.assignedTo(TaskStateApi.AssignedTo.builder().name("name").build())
					.status(TaskStateApi.Status.failed)
					.booking(new TaskStateApi.Booking("id",
						0L, 0L, 1, new ArrayList<>(), "requester"))
					.build();
				// Act
				fleetAdapterTaskServiceImpl.saveOrUpdateTaskState(taskStateApi);

				// Assert
				List<Alert> alerts = alertRepository.findAll();
				assertThat(alerts.size()).isEqualTo(0);
			}
		}
	}

	@Nested
	@DisplayName("saveTaskEventLog")
	class SaveTaskEventLog {

		@Nested
		@DisplayName("taskEventLog 생성")
		class CreateTaskEventLog {
			@Test
			@DisplayName("TaskEventLog를 생성한다.")
			void createTaskEventLog() {
				// Arrange
				TaskEventLogApi taskEventLogApi = TaskEventLogApi.builder()
					.taskEventLog(new TaskEventLog("id"))
					.log(new ArrayList<>())
					.phases(new HashMap<>())
					.build();

				//Act
				fleetAdapterTaskServiceImpl.saveTaskEventLog(taskEventLogApi);

				//Assert
				assertThat(logRepository.findAll().size()).isEqualTo(1);
			}
		}

		@Nested
		@DisplayName("Alert 생성")
		class CreateAlert {

			@Test
			@DisplayName("TaskEventLog의 log중 Tier가 ERROR인 log가 존재할 경우 Alert를 생성한다.")
			void createAlertWhenLog() {
				// Arrange
				List<TaskEventLogLog> logs = new ArrayList<>();
				logs.add(new TaskEventLogLog(1, Tier.error, 0L, "test"));
				logs.add(new TaskEventLogLog(2, Tier.info, 0L, "test"));
				TaskEventLogApi taskEventLogApi = TaskEventLogApi.builder()
					.taskEventLog(new TaskEventLog("id"))
					.log(logs)
					.phases(new HashMap<>())
					.build();

				//Act
				fleetAdapterTaskServiceImpl.saveTaskEventLog(taskEventLogApi);

				//Assert
				assertThat(alertRepository.findAll().size()).isEqualTo(1);
			}

			@Test
			@DisplayName("TaskEventLog의 log중 Tier가 ERROR가 아닌 log가 존재하지 않을 경우 Alert를 생성하지 않는다.")
			void notCreateAlertWhenLog() {
				// Arrange
				List<TaskEventLogLog> logs = new ArrayList<>();
				logs.add(new TaskEventLogLog(1, Tier.uninitialized, 0L, "test"));
				logs.add(new TaskEventLogLog(2, Tier.info, 0L, "test"));
				TaskEventLogApi taskEventLogApi = TaskEventLogApi.builder()
					.taskEventLog(new TaskEventLog("id"))
					.log(logs)
					.phases(new HashMap<>())
					.build();

				//Act
				fleetAdapterTaskServiceImpl.saveTaskEventLog(taskEventLogApi);

				//Assert
				assertThat(alertRepository.findAll().size()).isEqualTo(0);
			}

			@Test
			@DisplayName("TaskEventLog의 phases의 log중 Tier가 ERROR인 log가 존재할 경우 Alert를 생성한다.")
			void createAlertWhenPhasesLog() {
				// Arrange
				Map<String, PhasesApi> phases = new HashMap<>();
				List<TaskEventLogPhasesLog> logs = new ArrayList<>();
				logs.add(TaskEventLogPhasesLog.builder().tier(Tier.error).text("text").build());
				logs.add(TaskEventLogPhasesLog.builder().tier(Tier.info).text("text").build());
				phases.put("phase", PhasesApi.builder()
					.log(logs)
					.phases(TaskEventLogPhases.builder().build())
					.events(new ArrayList<>())
					.build());
				TaskEventLogApi taskEventLogApi = TaskEventLogApi.builder()
					.taskEventLog(new TaskEventLog("id"))
					.log(new ArrayList<>())
					.phases(phases)
					.build();

				//Act
				fleetAdapterTaskServiceImpl.saveTaskEventLog(taskEventLogApi);

				//Assert
				assertThat(alertRepository.findAll().size()).isEqualTo(1);
			}

			@Test
			@DisplayName("TaskEventLog의 phases의 log중 Tier가 ERROR가 아닌 log가 존재하지 않을 경우 Alert를 생성하지 않는다.")
			void notCreateAlertWhenPhasesLog() {
				// Arrange
				Map<String, PhasesApi> phases = new HashMap<>();
				List<TaskEventLogPhasesLog> logs = new ArrayList<>();
				logs.add(TaskEventLogPhasesLog.builder().tier(Tier.info).text("text").build());
				logs.add(TaskEventLogPhasesLog.builder().tier(Tier.warning).text("text").build());
				phases.put("phase", PhasesApi.builder()
					.log(logs)
					.phases(TaskEventLogPhases.builder().build())
					.events(new ArrayList<>())
					.build());
				TaskEventLogApi taskEventLogApi = TaskEventLogApi.builder()
					.taskEventLog(new TaskEventLog("id"))
					.log(new ArrayList<>())
					.phases(phases)
					.build();

				//Act
				fleetAdapterTaskServiceImpl.saveTaskEventLog(taskEventLogApi);

				//Assert
				assertThat(alertRepository.findAll().size()).isEqualTo(0);
			}


			@Test
			@DisplayName("TaskEventLog의 phases의 event의 log중 Tier가 ERROR인 log가 존재할 경우 Alert를 생성한다.")
			void createAlertWhenPhasesEventLog() {
				// Arrange
				List<TaskEventLogPhasesEventsLog> logs = new ArrayList<>();
				logs.add(TaskEventLogPhasesEventsLog.builder().tier(Tier.uninitialized).text("text").build());
				logs.add(TaskEventLogPhasesEventsLog.builder().tier(Tier.error).text("text").build());
				List<TaskEventLogPhasesEvents> events = new ArrayList<>();
				events.add(TaskEventLogPhasesEvents.builder()
					.event("event")
					.logs(logs)
					.build());
				Map<String, PhasesApi> phases = new HashMap<>();
				phases.put("phase", PhasesApi.builder()
					.log(new ArrayList<>())
					.phases(TaskEventLogPhases.builder().build())
					.events(events)
					.build());
				TaskEventLogApi taskEventLogApi = TaskEventLogApi.builder()
					.taskEventLog(new TaskEventLog("id"))
					.log(new ArrayList<>())
					.phases(phases)
					.build();

				//Act
				fleetAdapterTaskServiceImpl.saveTaskEventLog(taskEventLogApi);

				//Assert
				assertThat(alertRepository.findAll().size()).isEqualTo(1);
			}

			@Test
			@DisplayName("TaskEventLog의 log중 Tier가 ERROR가 아닌 log가 존재하지 않을 경우 Alert를 생성하지 않는다.")
			void notCreateAlertWhenPhasesEventLog() {
				// Arrange
				List<TaskEventLogPhasesEventsLog> logs = new ArrayList<>();
				logs.add(TaskEventLogPhasesEventsLog.builder().tier(Tier.warning).text("text").build());
				logs.add(TaskEventLogPhasesEventsLog.builder().tier(Tier.info).text("text").build());
				List<TaskEventLogPhasesEvents> events = new ArrayList<>();
				events.add(TaskEventLogPhasesEvents.builder()
					.event("event")
					.logs(logs)
					.build());
				Map<String, PhasesApi> phases = new HashMap<>();
				phases.put("phase", PhasesApi.builder()
					.log(new ArrayList<>())
					.phases(TaskEventLogPhases.builder().build())
					.events(events)
					.build());
				TaskEventLogApi taskEventLogApi = TaskEventLogApi.builder()
					.taskEventLog(new TaskEventLog("id"))
					.log(new ArrayList<>())
					.phases(phases)
					.build();

				//Act
				fleetAdapterTaskServiceImpl.saveTaskEventLog(taskEventLogApi);

				//Assert
				assertThat(alertRepository.findAll().size()).isEqualTo(0);
			}
		}
	}

}
