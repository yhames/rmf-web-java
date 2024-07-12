package com.rmf.apiserverjava.entity.fleets;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.rmf.apiserverjava.config.annotation.UnitTest;

@UnitTest
class FleetLogUnitTest {
	@Nested
	@DisplayName("FleetLogConstructor")
	class Constructor {
		@Nested
		@DisplayName("FleetLog(String name)")
		class FleetLogNameDataCreatedTest {
			@Test
			@DisplayName("인자들이 전달되었을 때 객체 생성에 성공한다.")
			void successTest() {
				//Arrange
				String name = "test";

				//Act
				FleetLog fleetLog = new FleetLog(name);

				//Assert
				assertNotNull(fleetLog);
			}
		}
	}
}
