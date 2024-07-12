package com.rmf.apiserverjava.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.entity.doors.DoorHealth;
import com.rmf.apiserverjava.entity.doors.DoorState;
import com.rmf.apiserverjava.entity.health.HealthStatus;
import com.rmf.apiserverjava.repository.DoorHealthRepository;
import com.rmf.apiserverjava.repository.DoorStateRepository;
import com.rmf.apiserverjava.rosmsgs.builtin.TimeMsg;
import com.rmf.apiserverjava.rosmsgs.door.DoorModeMsg;
import com.rmf.apiserverjava.rosmsgs.door.DoorStateMsg;

@UnitTest
class DoorServiceImplUnitTest {
	@Mock
	DoorStateRepository doorStateRepository;

	@Mock
	DoorHealthRepository doorHealthRepository;

	@InjectMocks
	DoorServiceImpl doorService;

	@Nested
	@DisplayName("getDoorStates")
	class GetDoorStates {
		@Test
		@DisplayName("조회에 성공할 경우 List<DoorState>를 반환한다.")
		void getDoorStatesFound() {
			// Arrange
			List<DoorState> doorStates = Stream.generate(() -> mock(DoorState.class))
				.limit(10)
				.toList();
			when(doorStateRepository.findAll()).thenReturn(doorStates);

			// Act
			List<DoorState> result = doorService.getDoorStates();

			// Assert
			assertThat(result).isEqualTo(doorStates);
			assertThat(result.size()).isEqualTo(doorStates.size());
		}
	}

	@Nested
	@DisplayName("getDoorState")
	class GetDoorState {
		@Test
		@DisplayName("조회에 성공할 경우 Optional<DoorState>를 반환한다.")
		void getDoorStateFound() {
			// Arrange
			DoorModeMsg doorModeMsg = DoorModeMsg.MODE_OPEN;
			TimeMsg timeMsg = TimeMsg.builder()
				.sec(1713951342)
				.nanoSec(599118563)
				.build();
			DoorStateMsg doorStateMsg = DoorStateMsg.builder()
				.doorTime(timeMsg)
				.doorName("test_door")
				.currentMode(doorModeMsg)
				.build();
			DoorState doorState = new DoorState(doorStateMsg.getDoorName(), doorStateMsg);
			when(doorStateRepository.findById(eq(doorStateMsg.getDoorName())))
				.thenReturn(Optional.of(doorState));

			// Act
			Optional<DoorState> result = doorService.getDoorState(doorStateMsg.getDoorName());

			// Assert
			assertThat(result.isPresent()).isTrue();
			assertThat(result.get()).isEqualTo(doorState);
		}

		@Test
		@DisplayName("조회에 실패할 경우 Optional.empty()를 반환한다.")
		void getDoorStateNotFound() {
			// Arrange
			String doorName = "test_door";
			when(doorStateRepository.findById(eq(doorName))).thenReturn(Optional.empty());

			// Act
			Optional<DoorState> result = doorService.getDoorState(doorName);

			// Assert
			assertThat(result.isEmpty()).isTrue();
		}
	}

	@Nested
	@DisplayName("getDoorHealth")
	class GetDoorHealth {
		@Test
		@DisplayName("조회에 성공할 경우 Optional<DoorHealth>를 반환한다.")
		void getDoorHealthFound() {
			// Arrange
			DoorHealth doorHealth = DoorHealth.builder()
				.id("test_door")
				.healthStatus(HealthStatus.Healthy)
				.healthMessage("Healthy")
				.build();
			when(doorHealthRepository.findById(eq(doorHealth.getId())))
				.thenReturn(Optional.of(doorHealth));

			// Act
			Optional<DoorHealth> result = doorService.getDoorHealth(doorHealth.getId());

			// Assert
			assertThat(result.isPresent()).isTrue();
			assertThat(result.get()).isEqualTo(doorHealth);
		}

		@Test
		@DisplayName("조회에 실패할 경우 Optional.empty()를 반환한다.")
		void getDoorHealthNotFound() {
			// Arrange
			String doorName = "test_door";
			when(doorHealthRepository.findById(eq(doorName))).thenReturn(Optional.empty());

			// Act
			Optional<DoorHealth> result = doorService.getDoorHealth(doorName);

			// Assert
			assertThat(result.isEmpty()).isTrue();
		}
	}

	@Nested
	@DisplayName("UpdateOrCreateDoorHealth")
	class UpdateOrCreateDoorHealth {
		@Test
		@DisplayName("기존의 DoorHealth와 HealthStatus가 다르면 DoorHealth를 업데이트합니다.")
		void update() {
			// Arrange
			DoorHealth doorHealth = DoorHealth.builder()
				.id("test_door")
				.healthStatus(HealthStatus.Healthy)
				.healthMessage("Healthy")
				.build();
			DoorHealth updateDoorHealth = DoorHealth.builder()
				.id("test_door")
				.healthStatus(HealthStatus.Unhealthy)
				.healthMessage("Unhealthy")
				.build();
			when(doorHealthRepository.findById(eq(doorHealth.getId())))
				.thenReturn(Optional.of(doorHealth));

			// Act
			Optional<DoorHealth> result = doorService.updateOrCreateDoorHealth(updateDoorHealth);

			// Assert
			assertThat(result.isPresent()).isTrue();
			assertThat(result.get().getHealthStatus()).isEqualTo(updateDoorHealth.getHealthStatus());
		}

		@Test
		@DisplayName("기존의 DoorHealth와 HealthStatus가 같으면 DoorHealth를 업데이트하지 않습니다.")
		void noUpdate() {
			// Arrange
			DoorHealth doorHealth = DoorHealth.builder()
				.id("test_door")
				.healthStatus(HealthStatus.Healthy)
				.healthMessage("Healthy")
				.build();
			DoorHealth updateDoorHealth = DoorHealth.builder()
				.id("test_door")
				.healthStatus(HealthStatus.Healthy)
				.healthMessage("Healthy")
				.build();
			when(doorHealthRepository.findById(eq(doorHealth.getId())))
				.thenReturn(Optional.of(doorHealth));

			// Act
			Optional<DoorHealth> result = doorService.updateOrCreateDoorHealth(updateDoorHealth);

			// Assert
			assertThat(result.isPresent()).isTrue();
			assertThat(result.get().getHealthStatus()).isEqualTo(updateDoorHealth.getHealthStatus());
		}

		@Test
		@DisplayName("기존의 DoorHealth가 없으면 DoorHealth를 생성합니다.")
		void create() {
			// Arrange
			DoorHealth updateDoorHealth = DoorHealth.builder()
				.id("test_door")
				.healthStatus(HealthStatus.Unhealthy)
				.healthMessage("Unhealthy")
				.build();
			when(doorHealthRepository.findById(eq(updateDoorHealth.getId())))
				.thenReturn(Optional.empty());

			// Act
			Optional<DoorHealth> result = doorService.updateOrCreateDoorHealth(updateDoorHealth);

			// Assert
			assertThat(result.isPresent()).isTrue();
			assertThat(result.get().getHealthStatus()).isEqualTo(updateDoorHealth.getHealthStatus());
		}
	}

	@Nested
	@DisplayName("UpdateOrCreateDoorHealth")
	class UpdateOrCreateDoorState {
		@Test
		@DisplayName("기존의 DoorState와 DoorStateMsg가 다르면 DoorState를 업데이트합니다.")
		void update() {
			// Arrange
			TimeMsg timeMsg = TimeMsg.builder()
				.sec(1713951342)
				.nanoSec(599118563)
				.build();
			DoorStateMsg doorStateMsg = DoorStateMsg.builder()
				.doorTime(timeMsg)
				.doorName("test_door")
				.currentMode(DoorModeMsg.MODE_OPEN)
				.build();
			DoorState doorState = new DoorState(doorStateMsg.getDoorName(), doorStateMsg);

			DoorStateMsg updatedDoorStateMsg = DoorStateMsg.builder()
				.doorTime(timeMsg)
				.doorName("test_door")
				.currentMode(DoorModeMsg.MODE_CLOSED)
				.build();
			DoorState updateDoorState = new DoorState(doorStateMsg.getDoorName(), updatedDoorStateMsg);

			when(doorStateRepository.findById(eq(doorStateMsg.getDoorName())))
				.thenReturn(Optional.of(doorState));

			// Act
			Optional<DoorState> result = doorService.updateOrCreateDoorState(updateDoorState);

			// Assert
			assertThat(result.isPresent()).isTrue();
			assertThat(result.get().getData().getCurrentMode()).isEqualTo(DoorModeMsg.MODE_CLOSED);
		}

		@Test
		@DisplayName("기존의 DoorState와 DoorStateMsg가 같으면 DoorState를 업데이트하지 않습니다.")
		void noUpdate() {
			DoorModeMsg doorModeMsg = DoorModeMsg.MODE_OPEN;
			TimeMsg timeMsg = TimeMsg.builder()
				.sec(1713951342)
				.nanoSec(599118563)
				.build();
			DoorStateMsg doorStateMsg = DoorStateMsg.builder()
				.doorTime(timeMsg)
				.doorName("test_door")
				.currentMode(doorModeMsg)
				.build();
			DoorState doorState = new DoorState(doorStateMsg.getDoorName(), doorStateMsg);

			DoorStateMsg updateDoorStateMsg = DoorStateMsg.builder()
				.doorTime(timeMsg)
				.doorName(doorStateMsg.getDoorName())
				.currentMode(doorModeMsg)
				.build();
			DoorState updateDoorState = new DoorState(doorStateMsg.getDoorName(), updateDoorStateMsg);

			when(doorStateRepository.findById(eq(doorStateMsg.getDoorName())))
				.thenReturn(Optional.of(doorState));

			// Act
			Optional<DoorState> result = doorService.updateOrCreateDoorState(updateDoorState);

			// Assert
			assertThat(result.isPresent()).isTrue();
			assertThat(result.get().getData().getCurrentMode()).isEqualTo(doorModeMsg);
		}
	}
}
