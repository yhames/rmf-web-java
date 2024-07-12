package com.rmf.apiserverjava.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.dto.beacons.CreateBeaconStateRequestDto;
import com.rmf.apiserverjava.entity.beacons.BeaconState;
import com.rmf.apiserverjava.global.exception.custom.NotFoundException;
import com.rmf.apiserverjava.repository.BeaconsRepository;
import com.rmf.apiserverjava.rxjava.eventbus.BeaconEvents;

import io.reactivex.rxjava3.subjects.PublishSubject;

@UnitTest
class BeaconsServiceImplUnitTest {

	@Mock
	BeaconsRepository beaconsRepository;

	@Mock
	BeaconEvents beaconEvents;

	@InjectMocks
	BeaconsServiceImpl beaconsServiceImpl;

	@Nested
	@DisplayName("getAllBeacons")
	class GetAllBeacons {
		@Test
		@DisplayName("조회에 성공할 경우 BeaconState 목록을 반환한다.")
		void getAllBeaconsFound() {
			//Arrange
			List<BeaconState> beaconStateList = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				beaconStateList.add(mock(BeaconState.class));
			}
			when(beaconsRepository.findAll()).thenReturn(beaconStateList);

			//Act
			List<BeaconState> result = beaconsServiceImpl.getAll();

			//Assert
			assertThat(result.size()).isEqualTo(beaconStateList.size());
		}
	}

	@Nested
	@DisplayName("getBeacon")
	class GetBeacon {
		@Test
		@DisplayName("조회에 성공할 경우 BeaconState를 반환한다.")
		void getBeaconFound() {
			//Arrange
			String id = "b1";
			BeaconState beaconState = mock(BeaconState.class);
			when(beaconsRepository.findById(eq(id))).thenReturn(Optional.of(beaconState));

			//Act
			Optional<BeaconState> result = beaconsServiceImpl.getOrNone(id);

			//Assert
			Assertions.assertThat(result.get()).isEqualTo(beaconState);
		}

		@Test
		@DisplayName("조회에 실패할 경우 Optional.empty()를 반환한다.")
		void getBeaconNotFound() {
			//Arrange
			String id = "b1";
			when(beaconsRepository.findById(eq(id))).thenReturn(Optional.empty());

			//Act
			Optional<BeaconState> result = beaconsServiceImpl.getOrNone(id);

			//Assert
			assertThat(result.isEmpty());
		}
	}

	@Nested
	@DisplayName("updateOrCreateBeacon")
	class UpdateOrCreateBeacon {
		@Test
		@DisplayName("id에 해당하는 BeaconState가 존재할 경우 beaconState 업데이트 로직을 호출한다.")
		void updateBeacon() {
			//Arrange
			CreateBeaconStateRequestDto beaconStateDto = mock(CreateBeaconStateRequestDto.class);
			when(beaconStateDto.getBeacon_id()).thenReturn("exist");
			when(beaconStateDto.getOnline()).thenReturn(true);
			when(beaconStateDto.getCategory()).thenReturn("category");
			when(beaconStateDto.getActivated()).thenReturn(true);
			when(beaconStateDto.getLevel()).thenReturn("level");
			BeaconState beaconState = mock(BeaconState.class);
			when(beaconEvents.getBeaconsEvent()).thenReturn(mock(PublishSubject.class));
			when(beaconsRepository.findById(eq(beaconStateDto.getBeacon_id()))).thenReturn(Optional.of(beaconState));

			//Act
			beaconsServiceImpl.updateOrCreate(beaconStateDto);

			//Assert
			verify(beaconState).updateBeaconState(1, "category", 1, "level");
		}

		@Test
		@DisplayName("id에 해당하는 BeaconState가 존재하지 않을 경우 BeaconState 생성 로직을 호출한다")
		void createBeacon() {
			//Arrange
			CreateBeaconStateRequestDto beaconStateDto = mock(CreateBeaconStateRequestDto.class);
			when(beaconStateDto.getBeacon_id()).thenReturn("created");
			when(beaconStateDto.getOnline()).thenReturn(true);
			when(beaconStateDto.getCategory()).thenReturn("category");
			when(beaconStateDto.getActivated()).thenReturn(true);
			when(beaconStateDto.getLevel()).thenReturn("level");
			when(beaconEvents.getBeaconsEvent()).thenReturn(mock(PublishSubject.class));
			when(beaconsRepository.findById(eq(beaconStateDto.getBeacon_id()))).thenReturn(Optional.empty());
			when(beaconsRepository.save(any(BeaconState.class))).thenReturn(mock(BeaconState.class));

			//Act
			beaconsServiceImpl.updateOrCreate(beaconStateDto);

			//Assert
			verify(beaconsRepository).save(any(BeaconState.class));
		}

		@Test
		@DisplayName("id에 해당하는 BeaconState가 존재하지 않으나, 저장에 실패할 경우 NotFoundException을 발생시킨다.")
		void createBeaconFailed() {
			//Arrange
			CreateBeaconStateRequestDto beaconStateDto = mock(CreateBeaconStateRequestDto.class);
			when(beaconStateDto.getBeacon_id()).thenReturn("created");
			when(beaconStateDto.getOnline()).thenReturn(true);
			when(beaconStateDto.getCategory()).thenReturn("category");
			when(beaconStateDto.getActivated()).thenReturn(true);
			when(beaconStateDto.getLevel()).thenReturn("level");
			when(beaconsRepository.findById(eq(beaconStateDto.getBeacon_id()))).thenReturn(Optional.empty());
			when(beaconsRepository.save(any(BeaconState.class))).thenReturn(null);

			//Act
			//Assert
			assertThatThrownBy(() -> beaconsServiceImpl.updateOrCreate(beaconStateDto))
				.isInstanceOf(NotFoundException.class);
		}
	}

}
