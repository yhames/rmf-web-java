package com.rmf.apiserverjava.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rmf.apiserverjava.dto.alerts.AlertResponseDto;
import com.rmf.apiserverjava.dto.alerts.CreateAlertDto;
import com.rmf.apiserverjava.entity.alerts.Alert;
import com.rmf.apiserverjava.repository.AlertRepository;
import com.rmf.apiserverjava.rxjava.eventbus.AlertEvents;
import com.rmf.apiserverjava.service.AlertService;
import com.rmf.apiserverjava.service.TaskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AlertServiceImpl.
 *
 * <p>
 *	AlertService의 구현체.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlertServiceImpl implements AlertService {
	private final AlertRepository alertRepository;
	private final TaskService taskService;
	private final AlertEvents alertEvents;

	/**
	 * repository에 alert 전체 조회를 요청한다.
	 */
	@Override
	public List<Alert> getAlerts() {
		return alertRepository.findAll();
	}

	/**
	 * <p>
	 * 	repository에 alert 생성을 요청한다.
	 * 	이미 존재하는 alert일 경우 엔티티에 update를 요청한다.
	 * 	생성에 실패할 경우 Optional.empty()를 반환한다.
	 * 	알림 생성에 성공하면 AlertsEvent를 발행한다.
	 * </p>
	 */
	@Override
	public Optional<Alert> createAlert(CreateAlertDto createAlertDto) {
		try {
			return createAlertLogic(createAlertDto);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * 트랜잭션 경계를 위해 createAlert에서 분리된 메서드
	 */
	@Transactional
	protected Optional<Alert> createAlertLogic(CreateAlertDto createAlertDto) {
		Optional<Alert> existAlertOptional = alertRepository.findById(createAlertDto.getAlertId());
		Alert result;
		if (existAlertOptional.isPresent()) {
			Alert existingAlert = existAlertOptional.get();
			existingAlert.update(createAlertDto.getCategory());
			result = existingAlert;
		} else {
			Alert newAlert = CreateAlertDto.MapStruct.INSTANCE.toEntity(createAlertDto);
			alertRepository.save(newAlert);
			result = newAlert;
		}
		alertEvents.getAlertsEvent().onNext(AlertResponseDto.MapStruct.INSTANCE.toDto(result));
		return Optional.of(result);
	}

	/**
	 * 사용자에게 인지된 알림을 생성하고 반환한다. 기존에 존재하던 알림은 삭제한다. id에 해당하는 알림이 존재하지 않을 경우 originalId를 기준으로 조회한다.
	 * 알림 갱신 및 Task 연동이 성공하면 AlertsEvent를 발행한다.
	 */
	@Override
	@Transactional
	public Optional<Alert> acknowledgeAlert(String alertId, String username) {
		Optional<Alert> alert = alertRepository.findById(alertId);
		long ackTimeMills = System.currentTimeMillis();

		if (!alert.isPresent()) {
			return getAlertByOriginalId(alertId);
		}

		Alert newAlert = alert.get().clone().acknowledge(ackTimeMills, username);
		alertRepository.save(newAlert);
		saveLogAcknowledgedTaskCompletion(alertId, username, ackTimeMills);
		alertRepository.delete(alert.get());
		alertEvents.getAlertsEvent().onNext(AlertResponseDto.MapStruct.INSTANCE.toDto(newAlert));
		return Optional.of(newAlert);
	}

	private void saveLogAcknowledgedTaskCompletion(String alertId, String username, long ackTimeMills) {
		taskService.saveLogAcknowledgedTaskCompletion(alertId, username, ackTimeMills);
	}

	/**
	 * originalId를 기준으로 alert를 조회한다. 존재하지 않을 경우 로그를 남기고 Optional.empty()를 반환한다.
	 */
	private Optional<Alert> getAlertByOriginalId(String originalId) {
		Optional<Alert> alert = alertRepository.findFirstByOriginalId(originalId);
		if (!alert.isPresent()) {
			log.error("Could not find alert with original ID {}", originalId);
		}
		return alert;
	}

	/**
	 * repository에 alert 단일 조회를 요청한다.
	 */
	@Override
	public Optional<Alert> getAlert(String alertId) {
		return alertRepository.findById(alertId);
	}
}
