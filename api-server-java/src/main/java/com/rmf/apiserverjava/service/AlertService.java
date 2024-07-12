package com.rmf.apiserverjava.service;

import java.util.List;
import java.util.Optional;

import com.rmf.apiserverjava.dto.alerts.CreateAlertDto;
import com.rmf.apiserverjava.entity.alerts.Alert;

/**
 * AlertService.
 *
 * <p>
 *	Alert에 대한 비즈니스 로직을 수행한다.
 * </p>
 */
public interface AlertService {
	/**
	 * Alert 단일 조회를 수행한다.
	 */
	Optional<Alert> getAlert(String alertId);

	/**
	 * Alert 전체 조회를 수행한다.
	 */
	List<Alert> getAlerts();

	/**
	 * Alert 생성을 수행한다.
	 */
	Optional<Alert> createAlert(CreateAlertDto createAlertDto);

	/**
	 * Alert의 acknowledge 이벤트를 처리한다.
	 */
	Optional<Alert> acknowledgeAlert(String alertId, String username);
}
