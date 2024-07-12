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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.entity.fleets.FleetLog;
import com.rmf.apiserverjava.entity.fleets.FleetLogRobots;
import com.rmf.apiserverjava.entity.fleets.FleetState;
import com.rmf.apiserverjava.repository.FleetLogLogRepository;
import com.rmf.apiserverjava.repository.FleetLogRepository;
import com.rmf.apiserverjava.repository.FleetLogRobotRepository;
import com.rmf.apiserverjava.repository.FleetLogRobotsLogRepository;
import com.rmf.apiserverjava.repository.FleetStateRepository;
import com.rmf.apiserverjava.rmfapi.fleet.FleetLogApi;
import com.rmf.apiserverjava.rmfapi.fleet.FleetStateApi;
import com.rmf.apiserverjava.rmfapi.fleet.LogEntryApi;
import com.rmf.apiserverjava.rxjava.eventbus.FleetEvents;

import io.reactivex.rxjava3.subjects.PublishSubject;

@UnitTest
class FleetAdapterFleetServiceImplUnitTest {

	@Mock
	private FleetStateRepository fleetStateRepository;

	@Mock
	private FleetLogRepository fleetLogRepository;

	@Mock
	private FleetLogLogRepository fleetLogLogRepository;

	@Mock
	private FleetLogRobotRepository fleetLogRobotRepository;

	@Mock
	private FleetLogRobotsLogRepository fleetLogRobotLogRepository;

	@Mock
	private FleetEvents fleetEvents;

	@InjectMocks
	private FleetAdapterFleetServiceImpl fleetAdapterFleetServiceImpl;

	@Captor
	ArgumentCaptor<List<FleetLogRobots>> captor;

	@Nested
	@DisplayName("saveOrUpdateFleetState")
	class SaveOrUpdateFleetState {
		@Test
		@DisplayName("이미 존재하는 FleetState를 업데이트한다.")
		void alreadyExist() {
			//Arrange
			FleetStateApi fleetStateApi = mock(FleetStateApi.class);
			FleetState fleetState = mock(FleetState.class);
			PublishSubject fleetStatesEvent = mock(PublishSubject.class);

			when(fleetStateRepository.findById(any())).thenReturn(Optional.of(fleetState));
			when(fleetEvents.getFleetStatesEvent()).thenReturn(fleetStatesEvent);

			//Act
			fleetAdapterFleetServiceImpl.saveOrUpdateFleetState(fleetStateApi);

			//Assert
			verify(fleetState).updateData(fleetStateApi);
			verify(fleetStateRepository, never()).save(any());
			verify(fleetStatesEvent).onNext(fleetStateApi);
		}

		@Test
		@DisplayName("새로운 FleetState를 생성한다.")
		void notExist() {
			//Arrange
			FleetStateApi fleetStateApi = mock(FleetStateApi.class);
			FleetState fleetState = mock(FleetState.class);
			PublishSubject fleetStatesEvent = mock(PublishSubject.class);

			when(fleetStateRepository.findById(any())).thenReturn(Optional.empty());
			when(fleetEvents.getFleetStatesEvent()).thenReturn(fleetStatesEvent);

			//Act
			fleetAdapterFleetServiceImpl.saveOrUpdateFleetState(fleetStateApi);

			//Assert
			verify(fleetState, never()).updateData(any());
			verify(fleetStateRepository).save(any());
			verify(fleetStatesEvent).onNext(fleetStateApi);
		}
	}

	@Nested
	@DisplayName("getOrCreateFleetLog")
	class CreateFleetLog {
		@Test
		@DisplayName("이미 존재하는 FleetLog를 반환한다.")
		void getOrCreateFleetLog() {
			//Arrange
			String name = "name";
			FleetLog fleetLog = mock(FleetLog.class);

			when(fleetLogRepository.findById(eq(name))).thenReturn(Optional.of(fleetLog));

			//Act
			FleetLog orCreateFleetLog = fleetAdapterFleetServiceImpl.getOrCreateFleetLog(name);

			//Assert
			orCreateFleetLog.equals(fleetLog);
		}

		@Test
		@DisplayName("기존에 존재하지 않는다면 새로운 FleetLog를 생성한다.")
		void notExist() {
			//Arrange
			String name = "name";
			when(fleetLogRepository.findById(eq(name))).thenReturn(Optional.empty());

			//Act
			fleetAdapterFleetServiceImpl.getOrCreateFleetLog(name);

			//Assert
			verify(fleetLogRepository).save(any());
		}
	}

	@Nested
	@DisplayName("getNewLogs")
	class GetNewLogs {
		@Test
		@DisplayName("FleetLogLog의 유니크 인덱스인 (fleet, seq) 조합을 활용해 새로운 로그를 필터링한다.")
		void filteringLogs() {
			//Arrange
			LogEntryApi exist = mock(LogEntryApi.class);
			LogEntryApi notExist = mock(LogEntryApi.class);
			List<Integer> existingSeq = List.of(1, 2);
			FleetLogApi fleetLogApi = mock(FleetLogApi.class);

			when(fleetLogApi.getLog()).thenReturn(List.of(exist, notExist));
			when(fleetLogLogRepository.findSeqByFleetIdAndSeqIn(any(), any())).thenReturn(existingSeq);
			when(notExist.getSeq()).thenReturn(3);
			when(exist.getSeq()).thenReturn(1);

			//Act
			List<LogEntryApi> newLogs = fleetAdapterFleetServiceImpl.getNewLogs(fleetLogApi);

			//Assert
			assertThat(newLogs).contains(notExist);
			assertThat(newLogs.contains(exist)).isFalse();
		}
	}

	@Nested
	@DisplayName("saveFleetLog")
	class SaveFleetLog {
		@Test
		@DisplayName("벌크 저장을 위해 saveAll을 호출한다.")
		void bulkInsert() {
			//Arrange
			LogEntryApi exist = mock(LogEntryApi.class);
			LogEntryApi notExist = mock(LogEntryApi.class);
			List<Integer> existingSeq = List.of(1, 2);
			FleetLogApi fleetLogApi = mock(FleetLogApi.class);

			when(fleetLogApi.getLog()).thenReturn(List.of(exist, notExist));
			when(fleetLogLogRepository.findSeqByFleetIdAndSeqIn(any(), any())).thenReturn(existingSeq);
			when(notExist.getSeq()).thenReturn(3);
			when(exist.getSeq()).thenReturn(1);
			when(fleetEvents.getFleetEventLogsEvent()).thenReturn(mock(PublishSubject.class));

			//Act
			fleetAdapterFleetServiceImpl.saveFleetLog(fleetLogApi);

			//Assert
			verify(fleetLogLogRepository).saveAll(any());
		}
	}

	@Nested
	@DisplayName("saveFleetRobotsWithLogs")
	class SaveFleetRobotsWithLogs {
		@Test
		@DisplayName("존재하지 않을 경우 새로운 FleetLogRobots를 생성한다.")
		void notExist() {
			//Arrange
			FleetLog fleetLog = mock(FleetLog.class);
			FleetLogRobots fleetLogRobots = mock(FleetLogRobots.class);
			FleetLogApi fleetLogApi = mock(FleetLogApi.class);
			Map<String, List<LogEntryApi>> robots = new HashMap<>();
			String robotName = "robot";
			robots.put(robotName, List.of(mock(LogEntryApi.class)));

			when(fleetLogApi.getRobots()).thenReturn(robots);
			when(fleetLogRobotRepository.findByFleetAndName(eq(fleetLog), eq(robotName)))
				.thenReturn(Optional.empty())
				.thenReturn(Optional.of(fleetLogRobots));

			//Act
			fleetAdapterFleetServiceImpl.saveFleetRobotsWithLogs(fleetLog, fleetLogApi);

			//Assert
			verify(fleetLogRobotRepository).saveAll(captor.capture());
			List<FleetLogRobots> capturedList = captor.getValue();
			assertThat(capturedList.size()).isEqualTo(1);
		}

		@Test
		@DisplayName("이미 존재하는 FleetLogRobots를 반환한다.")
		void exist() {
			//Arrange
			FleetLog fleetLog = mock(FleetLog.class);
			FleetLogRobots fleetLogRobots = mock(FleetLogRobots.class);
			FleetLogApi fleetLogApi = mock(FleetLogApi.class);
			Map<String, List<LogEntryApi>> robots = new HashMap<>();
			String robotName = "robot";
			robots.put(robotName, List.of(mock(LogEntryApi.class)));

			when(fleetLogApi.getRobots()).thenReturn(robots);
			when(fleetLogRobotRepository.findByFleetAndName(eq(fleetLog), eq(robotName))).thenReturn(
				Optional.of(fleetLogRobots));

			//Act
			fleetAdapterFleetServiceImpl.saveFleetRobotsWithLogs(fleetLog, fleetLogApi);

			//Assert
			verify(fleetLogRobotRepository).saveAll(any());
		}
	}

	@Nested
	@DisplayName("getNewRobotLogs")
	class GetNewRobotLogs {
		@Test
		@DisplayName("이미 존재하는 시퀀스는 필터링한다.")
		void existSeq() {
			//Arrange
			int robotId = 2;
			int existSeq = 1;

			FleetLogRobots fleetLogRobots = mock(FleetLogRobots.class);
			when(fleetLogRobots.getId()).thenReturn(robotId);

			List<LogEntryApi> logEntryApis = new ArrayList<>();
			LogEntryApi exist = mock(LogEntryApi.class);
			when(exist.getSeq()).thenReturn(existSeq);
			logEntryApis.add(exist);

			when(fleetLogRobotLogRepository.findSeqByRobotIdAndSeqIn(eq(robotId), any())).thenReturn(List.of(existSeq));

			//Act
			List<LogEntryApi> newRobotLogs = fleetAdapterFleetServiceImpl.getNewRobotLogs(logEntryApis, fleetLogRobots);

			//Assert
			assertThat(newRobotLogs).isEmpty();
		}

		@Test
		@DisplayName("존재하지 않는 시퀀스는 반환한다.")
		void notExistSeq() {
			//Arrange
			int robotId = 2;
			int existSeq = 1;
			int notExistSeq = 2;

			FleetLogRobots fleetLogRobots = mock(FleetLogRobots.class);
			when(fleetLogRobots.getId()).thenReturn(robotId);

			List<LogEntryApi> logEntryApis = new ArrayList<>();
			LogEntryApi notExist = mock(LogEntryApi.class);
			when(notExist.getSeq()).thenReturn(notExistSeq);
			logEntryApis.add(notExist);

			when(fleetLogRobotLogRepository.findSeqByRobotIdAndSeqIn(eq(robotId), any())).thenReturn(List.of(existSeq));

			//Act
			List<LogEntryApi> newRobotLogs = fleetAdapterFleetServiceImpl.getNewRobotLogs(logEntryApis, fleetLogRobots);

			//Assert
			assertThat(newRobotLogs.size()).isEqualTo(1);
		}
	}
}
