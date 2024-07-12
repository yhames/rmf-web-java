package com.rmf.apiserverjava.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.dto.alerts.CreateAlertDto;
import com.rmf.apiserverjava.entity.alerts.Alert;
import com.rmf.apiserverjava.repository.AlertRepository;
import com.rmf.apiserverjava.rxjava.eventbus.AlertEvents;
import com.rmf.apiserverjava.service.TaskService;

import io.reactivex.rxjava3.subjects.PublishSubject;

@UnitTest
class AlertServiceImplUnitTest {

	@Mock
	AlertRepository alertRepository;
	@Mock
	TaskService taskService;
	@Mock
	AlertEvents alertEvents;
	@InjectMocks
	AlertServiceImpl alertServiceImpl;

	@Nested
	@DisplayName("getAlerts")
	class GetAlerts {
		@Test
		@DisplayName("조회에 성공할 경우 List<Alert>를 반환한다.")
		void getAlertsFound() {
			//Arrange
			List<Alert> alerts = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				alerts.add(mock(Alert.class));
			}
			when(alertRepository.findAll()).thenReturn(alerts);

			//Act
			List<Alert> results = alertServiceImpl.getAlerts();

			//Assert
			assertThat(results).isEqualTo(alerts);
			assertThat(results.size()).isEqualTo(alerts.size());
		}
	}

	@Nested
	@DisplayName("getAlert")
	class GetAlert {
		@Test
		@DisplayName("조회에 성공할 경우 Optional<Alert>를 반환한다.")
		void getAlertFound() {
			//Arrange
			String id = "a1";
			Alert alert = mock(Alert.class);
			when(alertRepository.findById(eq(id))).thenReturn(Optional.of(alert));

			//Act
			Optional<Alert> result = alertServiceImpl.getAlert(id);

			//Assert
			assertThat(result.get()).isEqualTo(alert);
		}

		@Test
		@DisplayName("조회에 실패할 경우 Optional.empty()를 반환한다.")
		void getAlertNotFound() {
			//Arrange
			String id = "a1";
			when(alertRepository.findById(eq(id))).thenReturn(Optional.empty());

			//Act
			Optional<Alert> result = alertServiceImpl.getAlert(id);

			//Assert
			assertThat(result.isEmpty());
		}
	}

	@Nested
	@DisplayName("createAlert")
	class CreateAlert {
		@Test
		@DisplayName("id에 해당하는 alert가 존재할 경우 Alert의 update를 수행하고 Optional<Alert>를 반환한다.")
		void alertByIdExist() {
			//Arrange
			String id = "a1";
			Alert.Category category = Alert.Category.DEFAULT;
			Alert alert = mock(Alert.class);
			CreateAlertDto createAlertDto = mock(CreateAlertDto.class);

			when(alertRepository.findById(eq(id))).thenReturn(Optional.of(alert));
			when(createAlertDto.getCategory()).thenReturn(category);
			when(createAlertDto.getAlertId()).thenReturn(id);
			when(alertEvents.getAlertsEvent()).thenReturn(mock(PublishSubject.class));

			//Act
			Optional<Alert> result = alertServiceImpl.createAlert(createAlertDto);

			//Assert
			assertThat(result.get()).isNotNull();
			assertThat(result.get()).isEqualTo(alert);
			verify(alert).update(eq(category));
		}

		@Test
		@DisplayName("id에 해당하는 alert가 존재하지 않을 경우 새로운 Alert를 생성하고 Optional<Alert>를 반환한다.")
		void alertByIdNotExist() {
			//Arrange
			String id = "a1";
			Alert alert = mock(Alert.class);
			CreateAlertDto createAlertDto = mock(CreateAlertDto.class);

			when(alertRepository.findById(eq(id))).thenReturn(Optional.empty());
			when(createAlertDto.getAlertId()).thenReturn(id);
			when(alertEvents.getAlertsEvent()).thenReturn(mock(PublishSubject.class));

			//Act
			Optional<Alert> result = alertServiceImpl.createAlert(createAlertDto);

			//Assert
			assertThat(result.get()).isNotNull();
		}

		@Test
		@DisplayName("등록에 실패할경우 Optional.empty()를 반환한다.")
		void createAlertFailed() {
			//Arrange
			String id = "a1";
			CreateAlertDto createAlertDto = mock(CreateAlertDto.class);
			when(alertRepository.findById(eq(id))).thenThrow(new RuntimeException());
			when(createAlertDto.getAlertId()).thenReturn(id);

			//Act
			Optional<Alert> result = alertServiceImpl.createAlert(createAlertDto);

			//Assert
			assertThat(result.isEmpty());
		}
	}

	@Nested
	@DisplayName("acknowledgeAlert")
	class AcknowledgeAlert {
		@Test
		@DisplayName("이미 인지가 된 알림이 존재한다면 해당 알림을 반환한다.")
		void alreadyAcknowledge() {
			//Arrange
			String id = "a1";
			Alert alert = mock(Alert.class);
			when(alertRepository.findById(eq(id))).thenReturn(Optional.empty());
			when(alertRepository.findFirstByOriginalId(eq(id))).thenReturn(Optional.of(alert));

			//Act
			Optional<Alert> result = alertServiceImpl.acknowledgeAlert(id, "username");

			//Assert
			assertThat(result.get()).isEqualTo(alert);
		}

		@Test
		@DisplayName("조회에 실패할 경우 Optional.empty()를 반환한다.")
		void alertNotFound() {
			//Arrange
			String id = "a1";
			when(alertRepository.findById(eq(id))).thenReturn(Optional.empty());
			when(alertRepository.findFirstByOriginalId(eq(id))).thenReturn(Optional.empty());

			//Act
			Optional<Alert> result = alertServiceImpl.acknowledgeAlert(id, "username");

			//Assert
			assertThat(result.isEmpty());
		}

		@Test
		@DisplayName("알림이 인지되었을때 새로운 인지된 알림이 추가되고 기존 알림은 삭제되어야 한다.")
		void alertAcknowledgeSuccess() {
			//Arrange
			String id = "a1";
			Alert alert = mock(Alert.class);
			Alert newAlert = new Alert("id", "id", Alert.Category.FLEET, System.currentTimeMillis());
			when(alertRepository.findById(eq(id))).thenReturn(Optional.of(alert));
			when(alertEvents.getAlertsEvent()).thenReturn(mock(PublishSubject.class));

			when(alert.clone()).thenReturn(newAlert);

			//Act
			Optional<Alert> result = alertServiceImpl.acknowledgeAlert(id, "username");

			//Assert
			assertThat(result.get()).isEqualTo(newAlert);
			verify(alertRepository).save(eq(newAlert));
			verify(alertRepository).delete(eq(alert));
		}
	}
}
