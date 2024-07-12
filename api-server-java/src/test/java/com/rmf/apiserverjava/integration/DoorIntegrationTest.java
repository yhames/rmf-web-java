package com.rmf.apiserverjava.integration;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.rmf.apiserverjava.config.annotation.IntegrationTest;
import com.rmf.apiserverjava.dto.health.HealthResponseDto;
import com.rmf.apiserverjava.entity.doors.DoorHealth;
import com.rmf.apiserverjava.entity.doors.DoorState;
import com.rmf.apiserverjava.entity.health.HealthStatus;
import com.rmf.apiserverjava.global.utils.ObjectMapperUtils;
import com.rmf.apiserverjava.rosmsgs.builtin.TimeMsg;
import com.rmf.apiserverjava.rosmsgs.door.DoorModeMsg;
import com.rmf.apiserverjava.rosmsgs.door.DoorStateMsg;

import jakarta.persistence.EntityManager;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class DoorIntegrationTest {
	@Autowired
	EntityManager em;

	@Autowired
	private MockMvc mockMvc;

	@Nested
	@DisplayName("GET /doors")
	class GetDoorStates {
		@Test
		@DisplayName("DB에 DoorStates가 존재하지 않을 경우 빈 리스트를 반환한다.")
		void notExist() throws Exception {
			//Arrange
			String res = mockMvc.perform(get("/doors"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			List<DoorStateMsg> response = ObjectMapperUtils.MAPPER.readValue(res,
				ObjectMapperUtils.MAPPER.getTypeFactory().constructCollectionType(List.class, DoorStateMsg.class));

			//Assert
			assertThat(response).isNotNull();
			assertThat(response.size()).isEqualTo(0);
		}

		@Test
		@DisplayName("DB에 DoorState가 존재할 경우 전체 DoorStateMsg를 반환한다")
		void exist() throws Exception {
			//Arrange
			List<DoorState> doorStates = Stream.generate(() -> {
				DoorStateMsg doorStateMsg = DoorStateMsg.builder()
					.doorTime(new TimeMsg(1713951342, 599118563))
					.currentMode(DoorModeMsg.MODE_OPEN)
					.doorName(String.valueOf(Math.random() * 100))
					.build();
				return new DoorState(doorStateMsg.getDoorName(), doorStateMsg);
			}).limit(10).peek(doorState -> em.persist(doorState)).toList();
			em.flush();
			em.clear();

			//Act
			String res = mockMvc.perform(get("/doors"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
			List<DoorStateMsg> response = ObjectMapperUtils.MAPPER.readValue(res,
				ObjectMapperUtils.MAPPER.getTypeFactory().constructCollectionType(List.class, DoorStateMsg.class));

			//Assert
			assertThat(response).isNotNull();
			assertThat(response.size()).isEqualTo(doorStates.size());
		}
	}

	@Nested
	@DisplayName("GET /doors/{doorName}/state")
	class GetDoorState {

		private static final String URL_FORMAT = "/doors/%s/state";

		@Test
		@DisplayName("존재하지 않는 doorName으로 요청하면 404 NOT_FOUND를 반환한다.")
		void notExist() throws Exception {
			//Assert
			mockMvc.perform(get(String.format(URL_FORMAT, "notExist")))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("존재하는 doorName으로 요청하면 해당 DoorState를 반환한다.")
		void exist() throws Exception {
			//Arrange
			DoorStateMsg doorStateMsg = DoorStateMsg.builder()
				.doorTime(new TimeMsg(1713951342, 599118563))
				.currentMode(DoorModeMsg.MODE_OPEN)
				.doorName("door_name")
				.build();
			DoorState doorState = new DoorState(doorStateMsg.getDoorName(), doorStateMsg);
			em.persist(doorState);
			em.flush();
			em.clear();

			//Act
			String res = mockMvc.perform(get(String.format(URL_FORMAT, doorStateMsg.getDoorName())))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			DoorStateMsg response = ObjectMapperUtils.MAPPER.readValue(res, DoorStateMsg.class);


			//Assert
			assertThat(response).isNotNull();
			assertThat(response.getDoorName()).isEqualTo(doorStateMsg.getDoorName());
			assertThat(response.getDoorTime().getSec()).isEqualTo(doorStateMsg.getDoorTime().getSec());
			assertThat(response.getDoorTime().getNanoSec()).isEqualTo(doorStateMsg.getDoorTime().getNanoSec());
			assertThat(response.getCurrentMode().getValue()).isEqualTo(doorStateMsg.getCurrentMode().getValue());
		}
	}

	@Nested
	@DisplayName("GET /doors/{doorName}/health")
	class GetDoorHealth {

		private static final String URL_FORMAT = "/doors/%s/health";

		@Test
		@DisplayName("존재하지 않는 doorName으로 요청하면 404 NOT_FOUND를 반환한다.")
		void notExist() throws Exception {
			//Assert
			mockMvc.perform(get(String.format("/doors/%s/health", "notExist")))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("존재하는 doorName으로 요청하면 해당 DoorHealth를 반환한다.")
		void exist() throws Exception {
			//Arrange
			DoorHealth doorHealth = DoorHealth.builder()
				.id("test_door")
				.healthStatus(HealthStatus.Healthy)
				.healthMessage("Healthy")
				.build();
			em.persist(doorHealth);
			em.flush();
			em.clear();

			//Act
			String res = mockMvc.perform(get(String.format(URL_FORMAT, doorHealth.getId())))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			HealthResponseDto response = ObjectMapperUtils.MAPPER.readValue(res, HealthResponseDto.class);

			//Assert
			assertThat(response).isNotNull();
			assertThat(response.getId()).isEqualTo(doorHealth.getId());
			assertThat(response.getHealthStatus()).isEqualTo(doorHealth.getHealthStatus());
			assertThat(response.getHealthMessage()).isEqualTo(doorHealth.getHealthMessage());
		}
	}
}
