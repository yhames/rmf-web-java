package com.rmf.apiserverjava.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.rmf.apiserverjava.dto.alerts.AlertResponseDto;
import com.rmf.apiserverjava.dto.alerts.CreateAlertRequestDto;
import com.rmf.apiserverjava.dto.jwt.JwtUserInfoDto;
import com.rmf.apiserverjava.global.exception.custom.NotFoundException;

/**
 * AlertRestController.
 *
 * <p>
 *	Alert에 대한 REST API 엔드포인트를 정의한다.
 * </p>
 */
public interface AlertRestController {

	/**
	 * Alert의 전체 조회를 수행한다.
	 * */
	ResponseEntity<List<AlertResponseDto>> getAlerts();

	/**
	 * PK인 alertId를 기준으로 Alert의 단일 조회를 수행한다.
	 * @throws NotFoundException
	 * */
	ResponseEntity<AlertResponseDto> getAlert(String alertId);

	/**
	 * alertId 맟 category 정보를 통해 Alert를 생성한다.
	 * @throws NotFoundException
	 * */
	ResponseEntity<AlertResponseDto> createAlert(CreateAlertRequestDto requestDto);

	/**
	 * alertId를 기준으로 Alert의 acknowledge 이벤트를 처리한다.
	 * @throws NotFoundException
	 * */
	ResponseEntity<AlertResponseDto> acknowledgeAlert(String alertId, JwtUserInfoDto jwtUserInfoDto);
}
