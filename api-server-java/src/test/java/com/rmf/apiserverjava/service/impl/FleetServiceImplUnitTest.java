package com.rmf.apiserverjava.service.impl;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.dto.fleets.GetFleetLogsDto;
import com.rmf.apiserverjava.dto.time.TimeRangeDto;
import com.rmf.apiserverjava.entity.fleets.FleetLog;
import com.rmf.apiserverjava.entity.fleets.FleetLogLog;
import com.rmf.apiserverjava.entity.fleets.FleetLogRobots;
import com.rmf.apiserverjava.entity.fleets.FleetState;
import com.rmf.apiserverjava.repository.FleetLogLogRepository;
import com.rmf.apiserverjava.repository.FleetLogRepository;
import com.rmf.apiserverjava.repository.FleetLogRobotRepository;
import com.rmf.apiserverjava.repository.FleetStateRepository;
import com.rmf.apiserverjava.rmfapi.fleet.FleetLogApi;
import com.rmf.apiserverjava.rmfapi.fleet.FleetStateApi;

@UnitTest
class FleetServiceImplUnitTest {

	@Mock
	FleetStateRepository fleetStateRepository;

	@Mock
	FleetLogRepository fleetLogRepository;

	@Mock
	FleetLogLogRepository fleetLogLogRepository;

	@Mock
	FleetLogRobotRepository fleetLogRobotsRepository;

	@InjectMocks
	FleetServiceImpl fleetServiceImpl;

	@Nested
	@DisplayName("getAllFleets")
	class GetAllFleets {
		@Test
		@DisplayName("조회에 성공할 경우 List<FleetStateApi>를 반환한다.")
		void fleetStateFound() {
			//Arrange
			List<FleetState> fleetStates = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				FleetState fleetState = mock(FleetState.class);
				when(fleetState.getData()).thenReturn(mock(FleetStateApi.class));
				fleetStates.add(fleetState);
			}
			when(fleetStateRepository.findAll()).thenReturn(fleetStates);

			//Act
			List<FleetStateApi> results = fleetServiceImpl.getAllFleets();

			//Assert
			Assertions.assertThat(results.size()).isEqualTo(fleetStates.size());
			for (int i = 0; i < results.size(); i++) {
				Assertions.assertThat(results.get(i)).isInstanceOf(FleetStateApi.class);
			}
		}

		@Test
		@DisplayName("fleetState가 존재하지 않아도 비어있는 List<FleetStateApi>를 반환한다.")
		void fleetStateNotFound() {
			//Arrange
			List<FleetState> fleetStates = new ArrayList<>();
			when(fleetStateRepository.findAll()).thenReturn(fleetStates);

			//Act
			List<FleetStateApi> results = fleetServiceImpl.getAllFleets();

			//Assert
			Assertions.assertThat(results.size()).isEqualTo(0);
		}
	}

	@Nested
	@DisplayName("getFleetState")
	class GetFleetState {
		@Test
		@DisplayName("이름에 해당하는 fleetState 조회에 성공할 경우 Optional<FleetState>를 반환한다.")
		void getFleetStateFound() {
			//Arrange
			String fleetStateName = "fleetId";
			FleetState fleetState = mock(FleetState.class);
			FleetStateApi fleetStateApi = mock(FleetStateApi.class);
			when(fleetStateRepository.findById(eq(fleetStateName))).thenReturn(Optional.of(fleetState));
			when(fleetState.getData()).thenReturn(fleetStateApi);

			//Act
			Optional<FleetStateApi> result = fleetServiceImpl.getFleetState(fleetStateName);

			//Assert
			Assertions.assertThat(result.get()).isEqualTo(fleetStateApi);
		}

		@Test
		@DisplayName("이름에 해당하는 fleetState가 존재하지 않을 경우 Optional.empty()를 반환한다.")
		void getFleetStateNotFound() {
			//Arrange
			String fleetStateName = "fleetId";
			when(fleetStateRepository.findById(eq(fleetStateName))).thenReturn(Optional.empty());

			//Act
			Optional<FleetStateApi> result = fleetServiceImpl.getFleetState(fleetStateName);

			//Assert
			Assertions.assertThat(result).isEmpty();
		}
	}

	@Nested
	@DisplayName("getFleetLog")
	class GetFleetLog {
		@Test
		@DisplayName("이름에 해당하는 fleetLog가 존재할 경우 Optional<FleetLogApi>를 반환한다.")
		void getFleetLogFound() {
			//Arrange
			String fleetName = "fleetId";
			GetFleetLogsDto dto = mock(GetFleetLogsDto.class);

			when(dto.getFleetStateName()).thenReturn(fleetName);
			when(dto.getTimeRange()).thenReturn(mock(TimeRangeDto.class));
			when(fleetLogRepository.findByIdFetchRobots(eq(fleetName))).thenReturn(Optional.of(mock(FleetLog.class)));

			//Act
			Optional<FleetLogApi> result = fleetServiceImpl.getFleetLog(dto);

			//Assert
			Assertions.assertThat(result).isNotEmpty();
		}

		@Test
		@DisplayName("이름에 해당하는 fleetLog가 존재하지 않을 경우 Optional.empty()를 반환한다.")
		void getFleetLogNotFound() {
			//Arrange
			String fleetLogName = "fleetId";
			GetFleetLogsDto dto = mock(GetFleetLogsDto.class);
			when(fleetLogRepository.findByIdFetchRobots(eq(fleetLogName))).thenReturn(Optional.empty());
			when(dto.getFleetStateName()).thenReturn(fleetLogName);
			when(dto.getTimeRange()).thenReturn(mock(TimeRangeDto.class));

			//Act
			Optional<FleetLogApi> result = fleetServiceImpl.getFleetLog(dto);

			//Assert
			Assertions.assertThat(result).isEmpty();
		}
	}
}
