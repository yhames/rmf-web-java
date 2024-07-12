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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmf.apiserverjava.config.annotation.IntegrationTest;
import com.rmf.apiserverjava.dto.beacons.BeaconStateResponseDto;
import com.rmf.apiserverjava.dto.beacons.CreateBeaconStateRequestDto;
import com.rmf.apiserverjava.entity.beacons.BeaconState;

import jakarta.persistence.EntityManager;

@Transactional
@IntegrationTest
@AutoConfigureMockMvc
public class BeaconsIntegrationTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Nested
	@DisplayName("GET : /beacons")
	class GetAllBeacons {

		@Test
		@DisplayName("DB에 BeaconState가 존재하지 않을 경우 빈 목록을 반환한다.")
		void getAllBeaconsNotFound() throws Exception {
			//Arrange
			//Act
			String response = mockMvc.perform(get("/beacons"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			//Assert
			assertThat(response).isEqualTo("[]");
		}

		@Test
		@DisplayName("DB에 BeaconState가 존재할 경우 BeaconState 목록을 반환한다.")
		void getAllBeaconsFound() throws Exception {
			//Arrange
			int size = 10;

			for (int i = 0; i < size; i++) {
				BeaconState beaconState = new BeaconState("b" + i, 1, "category", 1, "level");
				em.persist(beaconState);
			}
			em.flush();
			em.clear();
			//Act
			String response = mockMvc.perform(get("/beacons"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			List<BeaconStateResponseDto> beaconStateDtoList = objectMapper.readValue(response, List.class);

			//Assert
			assertThat(beaconStateDtoList).isNotNull();
			assertThat(beaconStateDtoList.size()).isEqualTo(size);
		}
	}

	@Nested
	@DisplayName("GET : /beacons/{beaconId}")
	class GetBeacon {

		@Test
		@DisplayName("존재하지 않는 beaconId로 요청하면 404 NOT_FOUND 에러코드와 에러 메시지를 반환한다.")
		void notExist() throws Exception {
			//Act
			String content = mockMvc.perform(get("/beacons/notExistId"))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();

			//Assert
			assertThat(content).contains("Beacon with ID notExistId not found");
		}

		@Test
		@DisplayName("존재하는 beaconId로 요청하면 해당 BeaconStateDto를 반환한다.")
		void exist() throws Exception {
			//Arrange
			BeaconState beaconState
				= BeaconState.builder()
				.id("b1")
				.online(1)
				.category("category")
				.activated(1)
				.build();
			em.persist(beaconState);
			em.flush();
			em.clear();
			//Act
			String response = mockMvc.perform(get("/beacons/" + beaconState.getId()))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			BeaconStateResponseDto beaconStateDto = objectMapper.readValue(response, BeaconStateResponseDto.class);
			//Assert
			assertThat(beaconStateDto).isNotNull();
			assertThat(beaconStateDto.getId()).isEqualTo(beaconState.getId());
			assertThat(beaconStateDto.getOnline()).isEqualTo(beaconState.getOnline() == 1);
			assertThat(beaconStateDto.getCategory()).isEqualTo(beaconState.getCategory());
			assertThat(beaconStateDto.getActivated()).isEqualTo(beaconState.getActivated() == 1);
			assertThat(beaconStateDto.getLevel()).isEqualTo(beaconState.getLevel());
		}

	}

	@Nested
	@DisplayName("POST : /beacons")
	class SaveBeaconState {

		@Test
		@DisplayName("존재하지 않는 beaconId로 요청하면 새로운 BeaconState를 생성하고, 해당하는 BeaconStateDto를 반환한다.")
		void create() throws Exception {
			//Arrange
			CreateBeaconStateRequestDto beaconStateDto
				= new CreateBeaconStateRequestDto("b1", false, "category1", false, "level1");

			//Act
			String content = mockMvc.perform(post("/beacons")
				.contentType("application/x-www-form-urlencoded")
				.param("beacon_id", beaconStateDto.getBeacon_id())
				.param("online", String.valueOf(beaconStateDto.getOnline()))
				.param("category", beaconStateDto.getCategory())
				.param("activated", String.valueOf(beaconStateDto.getActivated()))
				.param("level", beaconStateDto.getLevel())
			).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

			//Assert
			BeaconState beaconStateDB = em.find(BeaconState.class, beaconStateDto.getBeacon_id());
			assertThat(beaconStateDB).isNotNull();
			assertThat(beaconStateDB.getId()).isEqualTo(beaconStateDto.getBeacon_id());
			assertThat(beaconStateDB.getOnline()).isEqualTo(beaconStateDto.getOnline() ? 1 : 0);
			assertThat(beaconStateDB.getCategory()).isEqualTo(beaconStateDto.getCategory());
			assertThat(beaconStateDB.getActivated()).isEqualTo(beaconStateDto.getActivated() ? 1 : 0);
			assertThat(beaconStateDB.getLevel()).isEqualTo(beaconStateDto.getLevel());

			BeaconStateResponseDto beaconStateDtoResponse
				= objectMapper.readValue(content, BeaconStateResponseDto.class);
			assertThat(beaconStateDtoResponse).isNotNull();
			assertThat(beaconStateDtoResponse.getId()).isEqualTo(beaconStateDto.getBeacon_id());
			assertThat(beaconStateDtoResponse.getOnline()).isEqualTo(beaconStateDto.getOnline());
			assertThat(beaconStateDtoResponse.getCategory()).isEqualTo(beaconStateDto.getCategory());
			assertThat(beaconStateDtoResponse.getActivated()).isEqualTo(beaconStateDto.getActivated());
			assertThat(beaconStateDtoResponse.getLevel()).isEqualTo(beaconStateDto.getLevel());
		}

		@Test
		@DisplayName("존재하는 beaconId로 요청하면 해당 BeaconState를 업데이트하고, 해당하는 BeaconStateDto를 반환한다.")
		void update() throws Exception {
			//Arrange
			BeaconState beaconState = new BeaconState("exist", 0, "c", 0, "l");
			em.persist(beaconState);
			em.flush();
			em.clear();
			CreateBeaconStateRequestDto beaconStateDto
				= new CreateBeaconStateRequestDto("exist", true, "category", true, "level");
			//Act
			String content = mockMvc.perform(post("/beacons")
				.contentType("application/x-www-form-urlencoded")
				.param("beacon_id", beaconStateDto.getBeacon_id())
				.param("online", String.valueOf(beaconStateDto.getOnline()))
				.param("category", beaconStateDto.getCategory())
				.param("activated", String.valueOf(beaconStateDto.getActivated()))
				.param("level", beaconStateDto.getLevel())
			).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
			//Assert
			BeaconState updatedBeaconState = em.find(BeaconState.class, beaconStateDto.getBeacon_id());
			assertThat(updatedBeaconState).isNotNull();
			assertThat(updatedBeaconState.getId()).isEqualTo(beaconStateDto.getBeacon_id());
			assertThat(updatedBeaconState.getOnline()).isEqualTo(beaconStateDto.getOnline() ? 1 : 0);
			assertThat(updatedBeaconState.getCategory()).isEqualTo(beaconStateDto.getCategory());
			assertThat(updatedBeaconState.getActivated()).isEqualTo(beaconStateDto.getActivated() ? 1 : 0);
			assertThat(updatedBeaconState.getLevel()).isEqualTo(beaconStateDto.getLevel());

			BeaconStateResponseDto beaconStateDtoResponse
				= objectMapper.readValue(content, BeaconStateResponseDto.class);
			assertThat(beaconStateDtoResponse).isNotNull();
			assertThat(beaconStateDtoResponse.getId()).isEqualTo(beaconStateDto.getBeacon_id());
			assertThat(beaconStateDtoResponse.getOnline()).isEqualTo(beaconStateDto.getOnline());
			assertThat(beaconStateDtoResponse.getCategory()).isEqualTo(beaconStateDto.getCategory());
			assertThat(beaconStateDtoResponse.getActivated()).isEqualTo(beaconStateDto.getActivated());
			assertThat(beaconStateDtoResponse.getLevel()).isEqualTo(beaconStateDto.getLevel());
		}

		@Test
		@DisplayName("생성 뒤 같은 beacon_id로 업데이트를 수행할 경우 정상적으로 업데이트한다.")
		void createAndUpdate() throws Exception {
			//Arrange
			CreateBeaconStateRequestDto beaconStateDto
				= new CreateBeaconStateRequestDto("exist", true, "category", true, "level");
			//Act
			mockMvc.perform(post("/beacons")
				.contentType("application/x-www-form-urlencoded")
				.param("beacon_id", beaconStateDto.getBeacon_id())
				.param("online", String.valueOf(beaconStateDto.getOnline()))
				.param("category", beaconStateDto.getCategory())
				.param("activated", String.valueOf(beaconStateDto.getActivated()))
				.param("level", beaconStateDto.getLevel())
			).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

			//Assert
			BeaconState createdBeaconState = em.find(BeaconState.class, beaconStateDto.getBeacon_id());
			assertThat(createdBeaconState).isNotNull();
			assertThat(createdBeaconState.getId()).isEqualTo(beaconStateDto.getBeacon_id());
			assertThat(createdBeaconState.getOnline()).isEqualTo(beaconStateDto.getOnline() ? 1 : 0);
			assertThat(createdBeaconState.getCategory()).isEqualTo(beaconStateDto.getCategory());
			assertThat(createdBeaconState.getActivated()).isEqualTo(beaconStateDto.getActivated() ? 1 : 0);
			assertThat(createdBeaconState.getLevel()).isEqualTo(beaconStateDto.getLevel());

			//Arrange
			CreateBeaconStateRequestDto beaconStateUpdateDto
				= new CreateBeaconStateRequestDto("exist", false, "updatedCategory", false, "updatedLevel");

			//Act
			String content = mockMvc.perform(post("/beacons")
				.contentType("application/x-www-form-urlencoded")
				.param("beacon_id", beaconStateUpdateDto.getBeacon_id())
				.param("online", String.valueOf(beaconStateUpdateDto.getOnline()))
				.param("category", beaconStateUpdateDto.getCategory())
				.param("activated", String.valueOf(beaconStateUpdateDto.getActivated()))
				.param("level", beaconStateUpdateDto.getLevel())
			).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
			//Assert
			BeaconState updatedBeaconState = em.find(BeaconState.class, beaconStateUpdateDto.getBeacon_id());
			assertThat(updatedBeaconState).isNotNull();
			assertThat(createdBeaconState.getId()).isEqualTo(beaconStateUpdateDto.getBeacon_id());
			assertThat(createdBeaconState.getOnline()).isEqualTo(beaconStateUpdateDto.getOnline() ? 1 : 0);
			assertThat(createdBeaconState.getCategory()).isEqualTo(beaconStateUpdateDto.getCategory());
			assertThat(createdBeaconState.getActivated()).isEqualTo(beaconStateUpdateDto.getActivated() ? 1 : 0);
			assertThat(createdBeaconState.getLevel()).isEqualTo(beaconStateUpdateDto.getLevel());

			BeaconStateResponseDto beaconStateDtoResponse
				= objectMapper.readValue(content, BeaconStateResponseDto.class);
			assertThat(beaconStateDtoResponse).isNotNull();
			assertThat(beaconStateDtoResponse.getId()).isEqualTo(beaconStateUpdateDto.getBeacon_id());
			assertThat(beaconStateDtoResponse.getOnline()).isEqualTo(beaconStateUpdateDto.getOnline());
			assertThat(beaconStateDtoResponse.getCategory()).isEqualTo(beaconStateUpdateDto.getCategory());
			assertThat(beaconStateDtoResponse.getActivated()).isEqualTo(beaconStateUpdateDto.getActivated());
			assertThat(beaconStateDtoResponse.getLevel()).isEqualTo(beaconStateUpdateDto.getLevel());
		}

		@Test
		@DisplayName("beacon_id를 포함하지 않으면 400 BAD_REQUEST 에러코드와 에러 메시지를 반환한다.")
		void noBeaconId() throws Exception {
			//Act
			String content = mockMvc.perform(post("/beacons")
				.contentType("application/x-www-form-urlencoded")
				.param("online", "true")
				.param("category", "category")
				.param("activated", "true")
				.param("level", "level")
			).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

			//Assert
			assertThat(content).isEqualTo("{\"detail\":\"Invalid Request\"}");
		}

		@Test
		@DisplayName("online을 포함하지 않으면 400 BAD_REQUEST 에러코드와 에러 메시지를 반환한다.")
		void noOnline() throws Exception {
			//Act
			String content = mockMvc.perform(post("/beacons")
				.contentType("application/x-www-form-urlencoded")
				.param("beacon_id", "beacon_id")
				.param("category", "category")
				.param("activated", "true")
				.param("level", "level")
			).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

			//Assert
			assertThat(content).isEqualTo("{\"detail\":\"Invalid Request\"}");
		}

		@Test
		@DisplayName("activated를 포함하지 않으면 400 BAD_REQUEST 에러코드와 에러 메시지를 반환한다.")
		void noCategory() throws Exception {
			//Act
			String content = mockMvc.perform(post("/beacons")
				.contentType("application/x-www-form-urlencoded")
				.param("beacon_id", "beacon_id")
				.param("online", "true")
				.param("category", "category")
				.param("level", "level")
			).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

			//Assert
			assertThat(content).isEqualTo("{\"detail\":\"Invalid Request\"}");
		}

		@Test
		@DisplayName("category와 level을 포함하지 않은 경우 정상적으로 생성에 완료한다")
		void noCategoryAndLevel() throws Exception {
			//Arrange
			CreateBeaconStateRequestDto beaconStateDto
				= new CreateBeaconStateRequestDto("exist", true, null, true, null);
			//Act
			mockMvc.perform(post("/beacons")
				.contentType("application/x-www-form-urlencoded")
				.param("beacon_id", beaconStateDto.getBeacon_id())
				.param("online", String.valueOf(beaconStateDto.getOnline()))
				.param("activated", String.valueOf(beaconStateDto.getActivated()))
			).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
			//Assert
			BeaconState updatedBeaconState = em.find(BeaconState.class, beaconStateDto.getBeacon_id());
			assertThat(updatedBeaconState).isNotNull();
			assertThat(updatedBeaconState.getId()).isEqualTo(beaconStateDto.getBeacon_id());
			assertThat(updatedBeaconState.getOnline()).isEqualTo(beaconStateDto.getOnline() ? 1 : 0);
			assertThat(updatedBeaconState.getCategory()).isNull();
			assertThat(updatedBeaconState.getActivated()).isEqualTo(beaconStateDto.getActivated() ? 1 : 0);
			assertThat(updatedBeaconState.getLevel()).isNull();
		}
	}
}
