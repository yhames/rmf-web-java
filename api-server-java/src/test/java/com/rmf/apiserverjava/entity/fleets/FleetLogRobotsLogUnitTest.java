package com.rmf.apiserverjava.entity.fleets;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.rmfapi.Tier;

@UnitTest
class FleetLogRobotsLogUnitTest {
	@Nested
	@DisplayName("FleetLogRobotsLogConstructor")
	class Constructor {
		@Nested
		@DisplayName("FleetLogRobotsLog")
		class FleetLogRobotsLogCreatedTest {
			@Test
			@DisplayName("데이터가 전달되었을 때 객체 생성에 성공한다.")
			void successTest() {
				//Arrange
				int seq = 1;
				long unixMillisTime = 1L;
				String text = "test";
				Tier tier = Tier.warning;

				//Act
				FleetLogRobotsLog fleetLogRobots = FleetLogRobotsLog.builder()
					.seq(seq)
					.tier(tier)
					.unixMillisTime(unixMillisTime)
					.text(text)
					.build();

				//Assert
				assertNotNull(fleetLogRobots);
			}
		}

		@Nested
		@DisplayName("setFleetLogRobotWithoutRelation")
		class SetFleetLogRobotWithoutRelation {
			@Test
			@DisplayName("연관관계 동기화를 하지 않고 fleetLogRobot를 설정한다.")
			void successTest() {
				//Arrange
				String fleetRobotName = "testFleet";
				FleetLogRobots fleetLogRobot = new FleetLogRobots(fleetRobotName);

				//Act
				FleetLogRobotsLog fleetLogRobotsLog = FleetLogRobotsLog.builder().build();
				fleetLogRobotsLog.setFleetLogRobotWithoutRelation(fleetLogRobot);

				//Assert
				assertNotNull(fleetLogRobotsLog);
				assertThat(fleetLogRobotsLog.getRobot()).isEqualTo(fleetLogRobot);
				assertThat(fleetLogRobot.getLogs().contains(fleetLogRobotsLog)).isFalse();
			}
		}
	}
}
