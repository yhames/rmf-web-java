package com.rmf.apiserverjava.controller.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rmf.apiserverjava.controller.AlertRestController;
import com.rmf.apiserverjava.dto.alerts.AlertResponseDto;
import com.rmf.apiserverjava.dto.alerts.CreateAlertDto;
import com.rmf.apiserverjava.dto.alerts.CreateAlertRequestDto;
import com.rmf.apiserverjava.dto.jwt.JwtUserInfoDto;
import com.rmf.apiserverjava.entity.alerts.Alert;
import com.rmf.apiserverjava.global.annotation.jwt.JwtUserInfo;
import com.rmf.apiserverjava.global.exception.custom.NotFoundException;
import com.rmf.apiserverjava.global.exception.hadler.GlobalExceptionHandlerAdvice.ErrorResponse;
import com.rmf.apiserverjava.service.AlertService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * AlertControllerImpl.
 *
 * <p>
 *     AlertRestController의 구현체.
 * </p>
 */
@Tag(name = "Alerts")
@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertRestControllerImpl implements AlertRestController {
	public static final String ALTER_WITH_ID_NOT_FOUND = "Alert with ID %s not found";
	public static final String CREATE_ALERT_FAILED = "Failed to create Alert with ID ";
	public static final String ACKNOWLEDGE_ALERT_FAILED = "Could acknowledge alert with ID ";

	private final AlertService alertService;

	@Override
	@Operation(summary = "Get Alerts", description = "Alerts 전체 조회를 요청합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Alerts 조회 성공"),
	})
	@GetMapping
	public ResponseEntity<List<AlertResponseDto>> getAlerts() {
		List<Alert> alerts = alertService.getAlerts();
		List<AlertResponseDto> alertsDto = alerts.stream()
			.map(AlertResponseDto.MapStruct.INSTANCE::toDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(alertsDto);
	}

	@Override
	@Operation(summary = "Get Alert", description = "Alert 단건 조회를 요청합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Alert 조회 성공"),
		@ApiResponse(responseCode = "404", description = "Alert 조회 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@GetMapping("/{alertId}")
	public ResponseEntity<AlertResponseDto> getAlert(@PathVariable String alertId) {
		Alert alert = alertService.getAlert(alertId)
			.orElseThrow(() -> new NotFoundException(String.format(ALTER_WITH_ID_NOT_FOUND, alertId)));
		return ResponseEntity.ok(AlertResponseDto.MapStruct.INSTANCE.toDto(alert));
	}

	@Override
	@Operation(summary = "Create Alert", description = "Alert 생성을 요청합니다. "
		+ "category는 default, task, fleet, robot 중 하나여야 합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Alert 생성 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 쿼리 파라미터의 전달로 Alert 생성 실패"),
		@ApiResponse(responseCode = "404", description = "Alert 생성 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@PostMapping
	public ResponseEntity<AlertResponseDto> createAlert(@ModelAttribute @Valid CreateAlertRequestDto requestDto) {
		CreateAlertDto createAlertDto = CreateAlertDto.MapStruct.INSTANCE.toDto(requestDto);
		Alert alert = alertService.createAlert(createAlertDto)
			.orElseThrow(() -> new NotFoundException(CREATE_ALERT_FAILED + createAlertDto.getAlertId()));
		return ResponseEntity.ok(AlertResponseDto.MapStruct.INSTANCE.toDto(alert));
	}

	@Override
	@Operation(summary = "Acknowledge Alert", description = "Alert에 대한 Acknowledge 이벤트를 요청합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Alert Ack 이벤트 성공"),
		@ApiResponse(responseCode = "404", description = "Alert Ack 이벤트 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@PostMapping("/{alertId}")
	public ResponseEntity<AlertResponseDto> acknowledgeAlert(
		@PathVariable String alertId,
		@Parameter(hidden = true) @JwtUserInfo JwtUserInfoDto jwtUserInfoDto) {
		Alert alert = alertService.acknowledgeAlert(alertId, jwtUserInfoDto.getUsername())
			.orElseThrow(() -> new NotFoundException(ACKNOWLEDGE_ALERT_FAILED + alertId));
		return ResponseEntity.ok(AlertResponseDto.MapStruct.INSTANCE.toDto(alert));
	}
}
