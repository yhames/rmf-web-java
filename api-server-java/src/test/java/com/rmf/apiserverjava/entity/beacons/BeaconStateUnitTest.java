package com.rmf.apiserverjava.entity.beacons;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.rmf.apiserverjava.config.annotation.UnitTest;

@UnitTest
class BeaconStateUnitTest {

	@Nested
	class BeaconStateTest {
		@Test
		@DisplayName("Builder를 통해 BeaconState 객체 생성에 성공한다")
		void buildEntityTest() {
			//Arrange
			//Act
			BeaconState beaconState = new BeaconState.BeaconStateBuilder()
				.id("beaconId")
				.online(1)
				.category("category")
				.activated(1)
				.level("level")
				.build();
			//Assert
			assertNotNull(beaconState);
		}
	}

	@Nested
	class MethodTest {
		@Test
		@DisplayName("updateBeaconState 메소드는 BeaconState의 필드를 업데이트한다")
		void updateBeaconStateTest() {
			//Arrange
			BeaconState beaconState = new BeaconState("beaconId", 1, "category", 1, "level");
			//Act
			beaconState.updateBeaconState(0, "category2", 0, "level2");
			//Assert
			assertEquals(beaconState.getOnline(), 0);
			assertEquals(beaconState.getCategory(), "category2");
			assertEquals(beaconState.getActivated(), 0);
			assertEquals(beaconState.getLevel(), "level2");
		}
	}
}
