package com.rmf.apiserverjava.controller.impl;

import static com.rmf.apiserverjava.global.exception.hadler.GlobalExceptionHandlerAdvice.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rmf.apiserverjava.controller.DoorRestController;
import com.rmf.apiserverjava.dto.health.HealthResponseDto;
import com.rmf.apiserverjava.entity.doors.DoorHealth;
import com.rmf.apiserverjava.entity.doors.DoorState;
import com.rmf.apiserverjava.global.exception.custom.NotFoundException;
import com.rmf.apiserverjava.rosmsgs.door.DoorStateMsg;
import com.rmf.apiserverjava.service.DoorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * DoorRestControllerImpl
 *
 * <p>
 *     DoorRestController의 구현체.
 * </p>
 */
@Tag(name = "Doors")
@Slf4j
@RestController
@RequestMapping("/doors")
@RequiredArgsConstructor
public class DoorRestControllerImpl implements DoorRestController {

	private static final String DOOR_WITH_ID_NOT_FOUND = "Door with ID %s not found";

	private final DoorService doorService;

	@Override
	@Operation(summary = "Get Doors", description = "전체 Doors의 State를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "DoorStates 조회 성공"),
	})
	@GetMapping()
	public ResponseEntity<List<DoorStateMsg>> getDoorStates() {
		List<DoorState> doorStates = doorService.getDoorStates();
		List<DoorStateMsg> doorStateMsgs = doorStates.stream().map(DoorState::getData).toList();
		return ResponseEntity.ok(doorStateMsgs);
	}

	@Override
	@Operation(summary = "Get Door State", description = "해당 Door에 대한 State를 조회합니다. (Available in socket.io)")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "DoorState 조회 성공"),
		@ApiResponse(responseCode = "404", description = "DoorState 조회 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@GetMapping("/{doorName}/state")
	public ResponseEntity<DoorStateMsg> getDoorState(@PathVariable String doorName) {
		DoorState doorState = doorService.getDoorState(doorName)
			.orElseThrow(() -> new NotFoundException(String.format(DOOR_WITH_ID_NOT_FOUND, doorName)));
		return ResponseEntity.ok(doorState.getData());
	}

	@Override
	@Operation(summary = "Get Door Health", description = "해당 Door에 대한 Health를 조회합니다. (Available in socket.io)")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "DoorHealth 조회 성공"),
		@ApiResponse(responseCode = "404", description = "DoorHealth 조회 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@GetMapping("/{doorName}/health")
	public ResponseEntity<HealthResponseDto> getDoorHealth(@PathVariable String doorName) {
		DoorHealth doorHealth = doorService.getDoorHealth(doorName)
			.orElseThrow(() -> new NotFoundException(String.format(DOOR_WITH_ID_NOT_FOUND, doorName)));
		return ResponseEntity.ok(HealthResponseDto.MapStruct.INSTANCE.toDto(doorHealth));
	}
}
