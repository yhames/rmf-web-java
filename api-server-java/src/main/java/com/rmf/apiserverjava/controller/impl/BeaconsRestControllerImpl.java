package com.rmf.apiserverjava.controller.impl;

import static com.rmf.apiserverjava.global.exception.hadler.GlobalExceptionHandlerAdvice.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rmf.apiserverjava.controller.BeaconsRestController;
import com.rmf.apiserverjava.dto.beacons.BeaconStateResponseDto;
import com.rmf.apiserverjava.dto.beacons.CreateBeaconStateRequestDto;
import com.rmf.apiserverjava.entity.beacons.BeaconState;
import com.rmf.apiserverjava.global.exception.custom.NotFoundException;
import com.rmf.apiserverjava.service.BeaconsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of the BeaconsRestController
 * */
@Tag(name = "Beacons")
@RestController
@RequestMapping("/beacons")
@RequiredArgsConstructor
public class BeaconsRestControllerImpl implements BeaconsRestController {

	private static final String BEACON_ID_NOT_FOUND = "Beacon with ID %s not found";

	private final BeaconsService beaconsService;

	@Override
	@Operation(summary = "Get Beacons", description = "전체 Beacons의 상태를 조회합니다. ")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Beacons 상태 조회 성공"),
	})
	@GetMapping("")
	public List<BeaconStateResponseDto> getBeacons() {
		List<BeaconState> beaconStateList = beaconsService.getAll();
		List<BeaconStateResponseDto> BeaconStateDtoList = beaconStateList.stream()
			.map(BeaconStateResponseDto.MapStruct.INSTANCE::toDto).collect(Collectors.toList());
		return BeaconStateDtoList;
	}

	@Override
	@Operation(summary = "Get Beacon", description = "해당 Beacon에 대한 상태를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "BeaconState 조회 성공"),
		@ApiResponse(responseCode = "404", description = "BeaconState 조회 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@GetMapping("/{beaconId}")
	public ResponseEntity<BeaconStateResponseDto> getBeacon(@PathVariable String beaconId) {
		BeaconState beaconState = beaconsService.getOrNone(beaconId).orElseThrow(
			(() -> new NotFoundException(String.format(BEACON_ID_NOT_FOUND, beaconId))));
		return ResponseEntity.ok(BeaconStateResponseDto.MapStruct.INSTANCE.toDto(beaconState));
	}

	@Override
	@Operation(summary = "Post Beacon", description = "해당 Beacon에 대한 상태를 수정하거나 생성합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "BeaconState 저장 성공"),
		@ApiResponse(responseCode = "404", description = "BeaconState 생성 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@PostMapping("")
	public ResponseEntity<BeaconStateResponseDto> saveBeaconState(
		@ModelAttribute @Valid CreateBeaconStateRequestDto beaconStateDto) {
		BeaconState created = beaconsService.updateOrCreate(beaconStateDto);
		return ResponseEntity.created(null).body(BeaconStateResponseDto.MapStruct.INSTANCE.toDto(created));
	}
}
