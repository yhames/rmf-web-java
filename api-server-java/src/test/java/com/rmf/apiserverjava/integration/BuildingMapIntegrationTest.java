package com.rmf.apiserverjava.integration;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.rmf.apiserverjava.config.annotation.IntegrationTest;
import com.rmf.apiserverjava.entity.buildingmaps.BuildingMap;
import com.rmf.apiserverjava.global.utils.ObjectMapperUtils;
import com.rmf.apiserverjava.rosmsgs.buildingmap.BuildingMapMsg;

import jakarta.persistence.EntityManager;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class BuildingMapIntegrationTest {
	@Autowired
	EntityManager em;

	@Autowired
	private MockMvc mockMvc;

	@Nested
	@DisplayName("GET /building_map")
	class GetBuildingMap {
		@Test
		@DisplayName("건물 맵 조회에 성공할 경우 맵 정보를 반환한다.")
		void exist() throws Exception {
			// Arrange
			BuildingMapMsg buildingMapMsg = new BuildingMapMsg("building", List.of(), List.of());
			BuildingMap buildingMap = new BuildingMap("building", buildingMapMsg);
			em.persist(buildingMap);
			em.flush();
			em.clear();

			// Act
			String res = mockMvc.perform(get("/building_map"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			BuildingMapMsg response = ObjectMapperUtils.MAPPER.readValue(res, BuildingMapMsg.class);

			// Assert
			assertThat(response).isNotNull();
			assertThat(response.getName()).isEqualTo(buildingMapMsg.getName());
			assertThat(response.getLevels()).isInstanceOf(List.class);
			assertThat(response.getLifts()).isInstanceOf(List.class);
		}

		@Test
		@DisplayName("건물 맵 정보가 없는 경우 404 NOT_FOUND를 반환한다.")
		void noExist() throws Exception {
			//Assert
			mockMvc.perform(get("/building_map")).andExpect(status().isNotFound());
		}
	}
}
