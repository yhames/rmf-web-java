package com.rmf.apiserverjava.entity.fleets;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.rmfapi.fleet.FleetStateApi;

@UnitTest
class FleetStateUnitTest {
	@Nested
	@DisplayName("FleetState")
	class FleetStateTest {
		@Nested
		@DisplayName("FleetStateConstructor")
		class Constructor {
			@Nested
			@DisplayName("FleetState(String name, FleetStateApi data)")
			class FleetStateNameDataCreatedTest {
				@Test
				@DisplayName("인자들이 전달되었을 때 객체 생성에 성공한다.")
				void successTest() {
					//Arrange
					String name = "test";
					FleetStateApi data = mock(FleetStateApi.class);

					//Act
					FleetState fleetState = new FleetState(name, data);
					//Assert
					assertNotNull(fleetState);
				}
			}
		}

		@Nested
		@DisplayName("updateData")
		class UpdateData {
			@Test
			@DisplayName("FleetStateApi 객체를 전달받아 data 필드를 업데이트한다.")
			void updateDataTest() {
				//Arrange
				FleetState fleetState = new FleetState();
				FleetStateApi data = mock(FleetStateApi.class);

				//Act
				fleetState.updateData(data);

				//Assert
				assertThat(fleetState.getData()).isEqualTo(data);
			}
		}
	}
}
