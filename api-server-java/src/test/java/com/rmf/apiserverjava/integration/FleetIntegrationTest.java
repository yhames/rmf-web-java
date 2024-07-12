package com.rmf.apiserverjava.integration;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.rmf.apiserverjava.config.annotation.IntegrationTest;
import com.rmf.apiserverjava.entity.fleets.FleetLog;
import com.rmf.apiserverjava.entity.fleets.FleetState;
import com.rmf.apiserverjava.rmfapi.fleet.FleetLogApi;
import com.rmf.apiserverjava.rmfapi.fleet.FleetStateApi;
import com.rmf.apiserverjava.rmfapi.fleet.Location2DApi;
import com.rmf.apiserverjava.rmfapi.fleet.RobotStateApi;

import jakarta.persistence.EntityManager;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class FleetIntegrationTest {
	@Autowired
	EntityManager em;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Nested
	@DisplayName("GET /fleets")
	class GetFleets {
		@Test
		@DisplayName("DB에 fleetState가 존재하지 않을 경우 빈 리스트를 반환한다.")
		void notExist() throws Exception {
			//Arrange
			//Act
			String res = mockMvc.perform(get("/fleets"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			List<FleetStateApi> response = objectMapper.readValue(res,
				objectMapper.getTypeFactory().constructCollectionType(List.class, FleetStateApi.class));

			//Assert
			assertThat(response).isNotNull();
			assertThat(response.size()).isEqualTo(0);
		}

		@Test
		@DisplayName("DB에 fleetState가 존재할 경우 fleet의 data 필드를 리스트로 반환한다")
		void exist() throws Exception {
			//Arrange
			int fleetSize = 3;
			int elementSize = 5;
			Map<String, RobotStateApi> map = new HashMap<>();
			for (int k = 0; k < fleetSize; k++) {
				for (int i = 0; i < elementSize; i++) {
					String id = String.valueOf(i);
					List<String> detailList = Arrays.asList("Error Code", "Battery");
					ArrayNode arrayNode = objectMapper.createArrayNode();
					for (String detail : detailList) {
						arrayNode.add(detail);
					}
					RobotStateApi.Issue issue = new RobotStateApi.Issue("issue" + i, arrayNode);
					Location2DApi l1 = new Location2DApi("L1", 10.4330539703369, -5.57509565353394, 1.32525944709778);
					RobotStateApi.Status2 status2 = RobotStateApi.Status2.idle;
					RobotStateApi robotStateApi = new RobotStateApi("robotState" + i, status2, "taskId" + i,
						System.currentTimeMillis(), l1, 0.5f, List.of(issue));
					map.put(id, robotStateApi);
				}
				FleetStateApi fleetStateApi = new FleetStateApi("fleetState" + k, map);
				FleetState fleetState = new FleetState("id" + k, fleetStateApi);
				em.persist(fleetState);
			}

			em.flush();
			em.clear();

			//Act
			String res = mockMvc.perform(get("/fleets"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			List<FleetStateApi> response = objectMapper.readValue(res,
				objectMapper.getTypeFactory().constructCollectionType(List.class, FleetStateApi.class));

			//Assert
			assertThat(response).isNotNull();
			assertThat(response.size()).isEqualTo(fleetSize);
			assertThat(response.get(0).getRobots().size()).isEqualTo(elementSize);
			assertThat(response.get(0).getRobots().get("0").getBattery()).isNotNull();
			assertThat(response.get(0).getRobots().get("0").getTaskId()).isNotNull();
			assertThat(response.get(0).getRobots().get("0").getName()).isNotNull();
			assertThat(response.get(0).getRobots().get("0").getUnixMillisTime()).isNotNull();
			assertThat(response.get(0).getRobots().get("0").getIssues()).isNotNull();
			assertThat(response.get(0).getRobots().get("0").getLocation()).isNotNull();
			assertThat(response.get(0).getRobots().get("0").getStatus()).isNotNull();
		}
	}

	@Nested
	@DisplayName("GET /fleets/{fleetStateName}/state")
	class GetFleetState {
		@Test
		@DisplayName("존재하지 않는 FleetStateName으로 요청하면 404 NOT_FOUND를 반환한다.")
		void notExist() throws Exception {
			//Arrange
			//Act
			//Assert
			String res = mockMvc.perform(get("/fleets/" + "NotExistName" + "/state"))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("존재하는 FleetStateName으로 요청하면 data인 FleetStateApi를 반환한다.")
		void exist() throws Exception {
			//Arrange
			FleetStateApi fleetStateApi = new FleetStateApi("fleetState", new HashMap<>());
			FleetState fleetState = new FleetState("11", fleetStateApi);
			em.persist(fleetState);
			em.flush();
			em.clear();

			//Act
			String res = mockMvc.perform(get("/fleets/" + fleetState.getName() + "/state"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			FleetStateApi response = objectMapper.readValue(res, FleetStateApi.class);

			//Assert
			assertThat(response).isNotNull();
			assertThat(response.getName()).isEqualTo(fleetStateApi.getName());
			assertThat(response.getRobots()).isEqualTo(fleetStateApi.getRobots());
		}
	}

	@Nested
	@DisplayName("GET /fleets/{fleetLogName}/log")
	class GetFleetLog {
		@Test
		@DisplayName("존재하지 않는 FleetLogName으로 요청하면 404 NOT_FOUND를 반환한다.")
		void notExist() throws Exception {
			//Arrange
			//Act
			//Assert
			String res = mockMvc.perform(get("/fleets/" + "NotExistName" + "/log"))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("존재하는 FleetLogName으로 요청하면 data인 FleetLogApi를 반환한다.")
		void exist() throws Exception {
			//Arrange
			FleetLog fleetLog = new FleetLog("fleetLog");
			em.persist(fleetLog);
			em.flush();
			em.clear();

			//Act
			String res = mockMvc.perform(get("/fleets/" + fleetLog.getName() + "/log"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			FleetLogApi response = objectMapper.readValue(res, FleetLogApi.class);

			//Assert
			assertThat(response).isNotNull();
			assertThat(response.getName()).isEqualTo(fleetLog.getName());
		}
	}
}
