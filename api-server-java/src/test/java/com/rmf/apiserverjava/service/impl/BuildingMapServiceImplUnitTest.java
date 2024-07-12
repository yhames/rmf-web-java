package com.rmf.apiserverjava.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.entity.buildingmaps.BuildingMap;
import com.rmf.apiserverjava.repository.BuildingMapRepository;
import com.rmf.apiserverjava.rosmsgs.buildingmap.BuildingMapMsg;

@UnitTest
class BuildingMapServiceImplUnitTest {

	@Mock
	BuildingMapRepository buildingMapRepository;

	@InjectMocks
	BuildingMapServiceImpl buildingMapServiceImpl;

	@Nested
	@DisplayName("getBuildingMap")
	class GetBuildingMap {
		@Test
		@DisplayName("조회에 성공할 경우 Optional<BuildingMap>를 반환한다.")
		void getBuildingMapFound() {
			//Arrange
			BuildingMapMsg buildingMapMsg = new BuildingMapMsg("building", List.of(), List.of());
			BuildingMap buildingMap = new BuildingMap("buliding", buildingMapMsg);
			when(buildingMapRepository.findAll()).thenReturn(List.of(buildingMap));

			//Act
			Optional<BuildingMap> result = buildingMapServiceImpl.getBuildingMap();

			//Assert
			assertThat(result.isPresent()).isTrue();
			assertThat(result.get()).isEqualTo(buildingMap);
		}

		@Test
		@DisplayName("조회에 실패할 경우 Optional.empty()를 반환한다.")
		void getBuildingMapNotFound() {
			//Arrange
			when(buildingMapRepository.findAll()).thenReturn(List.of());

			//Act
			Optional<BuildingMap> result = buildingMapServiceImpl.getBuildingMap();

			//Assert
			assertThat(result.isEmpty()).isTrue();
		}
	}

	@Nested
	@DisplayName("PostBuildingMap")
	class PostBuildingMap {
		@Test
		@DisplayName("기존의 건물 맵을 삭제 후 건물 맵을 저장하고 Optional<BuildingMap>를 반환한다.")
		void updateOrCreate() {
			// Arrange
			BuildingMap buildingMap = mock(BuildingMap.class);
			doNothing().when(buildingMapRepository).deleteAll();
			when(buildingMapRepository.save(buildingMap)).thenReturn(buildingMap);

			// Act
			Optional<BuildingMap> result = buildingMapServiceImpl.updateOrCreate(buildingMap);

			// Assert
			assertThat(result.isPresent()).isTrue();
			assertThat(result.get()).isEqualTo(buildingMap);
		}
	}
}
