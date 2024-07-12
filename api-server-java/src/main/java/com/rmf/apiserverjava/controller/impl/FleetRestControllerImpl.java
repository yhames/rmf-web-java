package com.rmf.apiserverjava.controller.impl;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rmf.apiserverjava.controller.FleetRestController;
import com.rmf.apiserverjava.dto.fleets.GetFleetLogsDto;
import com.rmf.apiserverjava.dto.time.TimeRangeDto;
import com.rmf.apiserverjava.global.exception.custom.NotFoundException;
import com.rmf.apiserverjava.global.exception.hadler.GlobalExceptionHandlerAdvice;
import com.rmf.apiserverjava.global.parser.LogBetweenParser;
import com.rmf.apiserverjava.rmfapi.fleet.FleetLogApi;
import com.rmf.apiserverjava.rmfapi.fleet.FleetStateApi;
import com.rmf.apiserverjava.service.FleetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * FleetRestControllerImpl.
 *
 * <p>
 *	FleetRestController의 구현체.
 * </p>
 */
@Tag(name = "Fleets")
@RestController
@RequestMapping("/fleets")
@RequiredArgsConstructor
public class FleetRestControllerImpl implements FleetRestController {
	public static final String FLEET_WITH_NAME_NOT_FOUND = "FleetState with ID %s not found";
	public static final String LOG_NOT_FOUND = "FleetLog with ID %s not found";
	public static final String DEFAULT_LOG_BETWEEN = "-60000";
	private final FleetService fleetService;
	private final LogBetweenParser logBetweenParser;

	@Override
	@Operation(summary = "Get FleetStates", description = "FleetState의 모든 내용의 조회를 요청합니다. 존재하지 않을 경우 빈 리스트를 반환합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "FleetState의 조회를 성공합니다."),
	})
	@GetMapping
	public ResponseEntity<List<FleetStateApi>> getFleets() {
		return ResponseEntity.ok(fleetService.getAllFleets());
	}

	@Override
	@Operation(summary = "Get FleetState", description = "PK에 해당하는 FleetState의 조회를 요청합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "PK에 해당하는 FleetState의 조회를 성공합니다."),
		@ApiResponse(responseCode = "404", description = "PK에 해당하는 FleetState가 존재하지 않습니다.",
			content = @Content(schema = @Schema(implementation = GlobalExceptionHandlerAdvice.ErrorResponse.class))),
	})
	@Parameter(name = "fleetStateName", description = "FleetState의 PK", in = ParameterIn.PATH)
	@GetMapping("/{fleetStateName}/state")
	public ResponseEntity<FleetStateApi> getFleetState(@PathVariable String fleetStateName) {
		FleetStateApi fleetStateApi = fleetService.getFleetState(fleetStateName)
			.orElseThrow(() -> new NotFoundException(String.format(FLEET_WITH_NAME_NOT_FOUND, fleetStateName)));
		return ResponseEntity.ok(fleetStateApi);
	}

	@Override
	@Operation(summary = "Get FleetLog", description = "PK에 해당하는 FleetLog의 조회를 요청합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "PK에 해당하는 FleetLog의 조회를 성공합니다."),
		@ApiResponse(responseCode = "404", description = "PK에 해당하는 FleetLog가 존재하지 않습니다.",
			content = @Content(schema = @Schema(implementation = GlobalExceptionHandlerAdvice.ErrorResponse.class))),
	})
	@Parameter(name = "fleetLogName", description = "FleetLog의 PK", in = ParameterIn.PATH)
	@Parameter(name = "between", description = "조회하려는 로그의 범위를 유닉스 밀리초로 전달합니다", in = ParameterIn.QUERY, examples = {
		@ExampleObject(name = "rangeTime", summary = "특정 시간 범위", value = "1500000000000,1700000000000"),
		@ExampleObject(name = "lastTime", summary = "마지막 60초", value = "-60000")
	})
	@GetMapping("/{fleetLogName}/log")
	public ResponseEntity<FleetLogApi> getFleetLog(
		@PathVariable String fleetLogName,
		@RequestParam(required = false, defaultValue = DEFAULT_LOG_BETWEEN) String between) {
		TimeRangeDto range = logBetweenParser.parseBetween(between);
		GetFleetLogsDto LogDto = GetFleetLogsDto.builder().timeRange(range).fleetStateName(fleetLogName).build();
		FleetLogApi fleetLogApi = fleetService.getFleetLog(LogDto)
			.orElseThrow(() -> new NotFoundException(String.format(LOG_NOT_FOUND, fleetLogName)));
		return ResponseEntity.ok(fleetLogApi);
	}
}
