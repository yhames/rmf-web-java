package com.rmf.apiserverjava.entity.fleets;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.rmfapi.Tier;

@UnitTest
class FleetLogLogUnitTest {

	@Nested
	@DisplayName("Constructor")
	class Constructor {
		@Test
		@DisplayName("전달받은 seq, tier, unixMillisTime, text로 FleetLogLog 객체를 생성한다.")
		void constructorSuccess() {
			//Arrange
			int seq = 1;
			Tier tier = Tier.error;
			long unixMillisTime = 1L;
			String text = "test";

			//Act
			FleetLogLog fleetLogLog = new FleetLogLog(seq, tier, unixMillisTime, text);

			//Assert
			assertThat(fleetLogLog.getSeq()).isEqualTo(seq);
			assertThat(fleetLogLog.getTier()).isEqualTo(tier);
			assertThat(fleetLogLog.getUnixMillisTime()).isEqualTo(unixMillisTime);
			assertThat(fleetLogLog.getText()).isEqualTo(text);
		}
	}

	@Nested
	@DisplayName("setFleetLogWithoutRelation")
	class SetFleetLogWithoutRelation {
		@Test
		@DisplayName("FleetLogLog의 fleet에 FleetLog을 설정하고 FleetLog의 logs에 FleetLogLog이 추가되지 않는다.")
		void setFleetLogWithoutRelation() {
			//Arrange
			FleetLog fleetLog = new FleetLog();
			FleetLogLog fleetLogLog = new FleetLogLog();

			//Act
			fleetLogLog.setFleetLogWithoutRelation(fleetLog);

			//Assert
			assertThat(fleetLogLog.getFleet()).isEqualTo(fleetLog);
			assertThat(fleetLog.getLogs().contains(fleetLogLog)).isFalse();
		}
	}
}
