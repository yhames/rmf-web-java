package com.rmf.apiserverjava.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.rmf.apiserverjava.config.annotation.IntegrationTest;
import com.rmf.apiserverjava.entity.fleets.FleetLog;
import com.rmf.apiserverjava.entity.fleets.FleetLogLog;
import com.rmf.apiserverjava.entity.fleets.FleetLogRobots;
import com.rmf.apiserverjava.entity.fleets.FleetLogRobotsLog;
import com.rmf.apiserverjava.entity.fleets.FleetState;
import com.rmf.apiserverjava.repository.FleetLogLogRepository;
import com.rmf.apiserverjava.repository.FleetLogRepository;
import com.rmf.apiserverjava.repository.FleetLogRobotRepository;
import com.rmf.apiserverjava.repository.FleetLogRobotsLogRepository;
import com.rmf.apiserverjava.repository.FleetStateRepository;
import com.rmf.apiserverjava.rmfapi.Tier;
import com.rmf.apiserverjava.rmfapi.fleet.FleetLogApi;
import com.rmf.apiserverjava.rmfapi.fleet.FleetStateApi;
import com.rmf.apiserverjava.rmfapi.fleet.LogEntryApi;
import com.rmf.apiserverjava.service.impl.FleetAdapterFleetServiceImpl;

import jakarta.persistence.EntityManager;

@IntegrationTest
@Transactional
public class FleetAdapterFleetServiceImplIntegrationTest {
	@Autowired
	EntityManager em;

	@Autowired
	FleetAdapterFleetServiceImpl fleetAdapterFleetServiceImpl;

	@Autowired
	FleetLogRepository fleetLogRepository;

	@Autowired
	FleetLogLogRepository fleetLogLogRepository;

	@Autowired
	FleetLogRobotsLogRepository fleetLogRobotsLogRepository;

	@Autowired
	FleetLogRobotRepository fleetLogRobotRepository;

	@Autowired
	FleetStateRepository fleetStateRepository;

	@Nested
	@DisplayName("saveFleetLog")
	class SaveFleetLog {
		@Test
		@DisplayName("DB에 name에 해당하는 FleetLog가 존재하지 않을 경우 새로운 FleetLog를 생성한다")
		void newFleetLog() throws Exception {
			//Arrange
			String name = "test";
			List<LogEntryApi> logs = new ArrayList<>();
			Map<String, List<LogEntryApi>> robots = new HashMap<>();
			FleetLogApi fleetLogApi = new FleetLogApi(name, logs, robots);

			//Act
			fleetAdapterFleetServiceImpl.saveFleetLog(fleetLogApi);
			Optional<FleetLog> fleetLog = fleetLogRepository.findById(name);

			//Assert
			assertThat(fleetLog.isPresent()).isTrue();
		}

		@Test
		@DisplayName("DB에 같은 name의 FleetLog가 이미 존재할 경우 기존의 FleetLog를 반환한다")
		void existFleetLog() throws Exception {
			//Arrange
			String name = "test";
			List<LogEntryApi> logs = new ArrayList<>();
			Map<String, List<LogEntryApi>> robots = new HashMap<>();
			FleetLogApi fleetLogApi = new FleetLogApi(name, logs, robots);
			FleetLog existingFleetLog = new FleetLog(name);
			em.persist(existingFleetLog);
			em.flush();

			//Act
			fleetAdapterFleetServiceImpl.saveFleetLog(fleetLogApi);
			Optional<FleetLog> fleetLog = fleetLogRepository.findById(name);

			//Assert
			assertThat(fleetLog.isPresent()).isTrue();
			assertThat(fleetLog.get()).isEqualTo(existingFleetLog);
		}

		@Test
		@DisplayName("DB에 존재하지 않는 FleetLog의 FleetLogLog는 모두 생성되어야 한다")
		void saveAllFleetLogLog() throws Exception {
			//Arrange
			String name = "test";
			int size = 10;
			List<LogEntryApi> logs = new ArrayList<>();
			for (int i = 0; i < size; i++) {
				logs.add(new LogEntryApi(i, Tier.error, System.currentTimeMillis(), "test" + i));
			}
			Map<String, List<LogEntryApi>> robots = new HashMap<>();
			FleetLogApi fleetLogApi = new FleetLogApi(name, logs, robots);

			//Act
			fleetAdapterFleetServiceImpl.saveFleetLog(fleetLogApi);
			List<FleetLogLog> logResults = fleetLogLogRepository.findAll();

			//Assert
			assertThat(logResults.size()).isEqualTo(size);
			logResults.stream().forEach(log -> assertThat(log.getFleet().getName()).isEqualTo(name));
		}

		@Test
		@DisplayName("DB에 존재하는 FleetLog의 FleetLogLog는 다시 생성되서는 안된다")
		void notSaveExistingFleetLogLog() throws Exception {
			//Arrange
			String name = "test";
			int size = 10;
			int notExistSize = 5;
			long newLogTime = 0L;
			List<LogEntryApi> logs = new ArrayList<>();
			Map<String, List<LogEntryApi>> robots = new HashMap<>();
			FleetLog existingFleetLog = new FleetLog(name);
			em.persist(existingFleetLog);
			for (int i = 0; i < size; i++) {
				logs.add(new LogEntryApi(i, Tier.error, newLogTime, "test" + i));
			}
			for (int i = 0; i < notExistSize; i++) {
				FleetLogLog fleetLogLog = new FleetLogLog(i, Tier.error, System.currentTimeMillis(), "test" + i);
				fleetLogLog.setFleetLogWithoutRelation(existingFleetLog);
				em.persist(fleetLogLog);
			}
			em.flush();
			FleetLogApi fleetLogApi = new FleetLogApi(name, logs, robots);

			//Act
			fleetAdapterFleetServiceImpl.saveFleetLog(fleetLogApi);
			List<FleetLogLog> logResults = fleetLogLogRepository.findAll();

			//Assert
			assertThat(logResults.size()).isEqualTo(size);
			long count = logResults.stream().filter(log -> log.getUnixMillisTime() != newLogTime).count();
			assertThat(count).isEqualTo(notExistSize);
		}

		@Test
		@DisplayName("DB에 전달받은 FleetLogRobots과 FleetLogRobotsLog가 존재하지 않다면 모두 생성되어야 한다")
		void saveFleetLogRobotsAndLogs() throws Exception {
			//Arrange
			String name = "test";
			FleetLog existingFleetLog = new FleetLog(name);
			em.persist(existingFleetLog);
			em.flush();

			int robotSize = 5;
			int robotLogSize = 10;
			List<LogEntryApi> logs = new ArrayList<>();
			Map<String, List<LogEntryApi>> robots = new HashMap<>();

			for (int i = 0; i < robotSize; i++) {
				String robotName = "robot" + i;
				List<LogEntryApi> robotLogs = new ArrayList<>();
				for (int j = 0; j < robotLogSize; j++) {
					robotLogs.add(new LogEntryApi(j, Tier.error, System.currentTimeMillis(), robotName + "log"));
				}
				robots.put(robotName, robotLogs);
			}
			FleetLogApi fleetLogApi = new FleetLogApi(name, logs, robots);

			//Act
			fleetAdapterFleetServiceImpl.saveFleetLog(fleetLogApi);
			List<FleetLogRobots> fleetLogRobots = fleetLogRobotRepository.findAll();
			List<FleetLogRobotsLog> fleetLogRobotsLog = fleetLogRobotsLogRepository.findAll();

			//Assert
			assertThat(fleetLogRobots.size()).isEqualTo(robotSize);
			assertThat(fleetLogRobotsLog.size()).isEqualTo(robotSize * robotLogSize);
			System.out.println(fleetLogRobots.size());
		}

		@Test
		@DisplayName("DB에 존재하는 FleetLogRobots과 동일 FleetLogRobotsLog Seq는 모두 생성되지 않아야 한다")
		void notSaveExistRobotsAndLogs() throws Exception {
			//Arrange
			String name = "test";
			FleetLog existingFleetLog = new FleetLog(name);
			em.persist(existingFleetLog);

			FleetLogRobots existingFleetLogRobots = new FleetLogRobots("robot0");
			existingFleetLogRobots.setFleetLogWithoutRelation(existingFleetLog);
			em.persist(existingFleetLogRobots);

			FleetLogRobotsLog existingFleetLogRobotsLog = new FleetLogRobotsLog(0, Tier.error,
				System.currentTimeMillis(), "robot0log");
			existingFleetLogRobotsLog.setFleetLogRobotWithoutRelation(existingFleetLogRobots);
			em.persist(existingFleetLogRobotsLog);
			em.flush();

			int robotSize = 5;
			int robotLogSize = 10;
			List<LogEntryApi> logs = new ArrayList<>();
			Map<String, List<LogEntryApi>> robots = new HashMap<>();

			for (int i = 0; i < robotSize; i++) {
				String robotName = "robot" + i;
				List<LogEntryApi> robotLogs = new ArrayList<>();
				for (int j = 0; j < robotLogSize; j++) {
					robotLogs.add(new LogEntryApi(j, Tier.error, System.currentTimeMillis(), robotName + "log"));
				}
				robots.put(robotName, robotLogs);
			}
			FleetLogApi fleetLogApi = new FleetLogApi(name, logs, robots);

			//Act
			fleetAdapterFleetServiceImpl.saveFleetLog(fleetLogApi);
			List<FleetLogRobots> fleetLogRobots = fleetLogRobotRepository.findAll();
			List<FleetLogRobotsLog> fleetLogRobotsLog = fleetLogRobotsLogRepository.findAll();

			//Assert
			assertThat(fleetLogRobots.size()).isEqualTo(robotSize);
			assertThat(fleetLogRobotsLog.size()).isEqualTo(robotSize * robotLogSize);
			System.out.println(fleetLogRobots.size());
		}
	}

	@Nested
	@DisplayName("saveOrUpdateFleetState")
	class SaveOrUpdateFleetState {
		@Test
		@DisplayName("DB에 name에 해당하는 fleetState 존재하지 않을 경우 새로운 fleetState 생성한다")
		void newFleetState() throws Exception {
			//Arrange
			String name = "test";
			FleetStateApi fleetStateApi = new FleetStateApi(name, new HashMap<>());

			//Act
			fleetAdapterFleetServiceImpl.saveOrUpdateFleetState(fleetStateApi);
			Optional<FleetState> fleetState = fleetStateRepository.findById(name);

			//Assert
			assertThat(fleetState.isPresent()).isTrue();
		}

		@Test
		@DisplayName("DB에 같은 name의 fleetState가 이미 존재할 경우 기존의 fleetState를 반환한다")
		void existFleetState() throws Exception {
			//Arrange
			String name = "test";
			FleetStateApi fleetStateApi = new FleetStateApi(name, new HashMap<>());

			FleetState existingFleetState = new FleetState(name, new FleetStateApi(name, new HashMap<>()));
			em.persist(existingFleetState);
			em.flush();

			//Act
			fleetAdapterFleetServiceImpl.saveOrUpdateFleetState(fleetStateApi);
			Optional<FleetState> fleetState = fleetStateRepository.findById(name);

			//Assert
			assertThat(fleetState.isPresent()).isTrue();
			assertThat(fleetState.get()).isEqualTo(existingFleetState);
		}
	}
}
