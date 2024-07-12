package com.rmf.apiserverjava.entity.fleets;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.rmf.apiserverjava.config.annotation.UnitTest;

@UnitTest
class FleetLogRobotsUnitTest {
	@Nested
	@DisplayName("FleetLogRobotsConstructor")
	class Constructor {
		@Nested
		@DisplayName("FleetLogRobots(String name)")
		class FleetLogRobotNameDataCreatedTest {
			@Test
			@DisplayName("이름이 전달되었을 때 객체 생성에 성공한다.")
			void successTest() {
				//Arrange
				String name = "test";

				//Act
				FleetLogRobots fleetLogRobots = new FleetLogRobots(name);

				//Assert
				assertNotNull(fleetLogRobots);
			}
		}

		@Nested
		@DisplayName("setFleetLogWithoutRelation")
		class SetFleetLogWithoutRelation {
			@Test
			@DisplayName("연관관계 동기화를 하지 않고 fleetLog를 설정한다.")
			void successTest() {
				//Arrange
				String name = "test";
				String fleetName = "testFleet";
				FleetLog fleetLog = new FleetLog(fleetName);

				//Act
				FleetLogRobots fleetLogRobots = new FleetLogRobots(name);
				fleetLogRobots.setFleetLogWithoutRelation(fleetLog);

				//Assert
				assertNotNull(fleetLogRobots);
				assertThat(fleetLogRobots.getFleet()).isEqualTo(fleetLog);
				assertThat(fleetLog.getRobots().contains(fleetLogRobots)).isFalse();
			}
		}
	}
}
