package com.rmf.apiserverjava.controller.impl;

import static com.rmf.apiserverjava.global.exception.hadler.GlobalExceptionHandlerAdvice.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rmf.apiserverjava.controller.BuildingMapRestController;
import com.rmf.apiserverjava.entity.buildingmaps.BuildingMap;
import com.rmf.apiserverjava.global.exception.custom.NotFoundException;
import com.rmf.apiserverjava.rosmsgs.buildingmap.BuildingMapMsg;
import com.rmf.apiserverjava.service.impl.BuildingMapServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * BuildingMapRestControllerImpl
 *
 * <p>
 *     BuildingMapRestController의 구현체.
 * </p>
 */
@Tag(name = "Building")
@RestController
@RequiredArgsConstructor
@RequestMapping("/building_map")
public class BuildingMapRestControllerImpl implements BuildingMapRestController {

	private static final String NO_BUILDING_MAP = "No BuildingMap Exist";

	private final BuildingMapServiceImpl buildingMapService;

	@Override
	@Operation(summary = "Get Building Map", description = "건물 맵 데이터를 조회합니다. (Available in socket.io)")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Building Map 조회 성공"),
		@ApiResponse(responseCode = "404", description = "Building Map 조회 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@GetMapping()
	public ResponseEntity<BuildingMapMsg> getBuildingMap() {
		BuildingMap buildingMap = buildingMapService.getBuildingMap()
			.orElseThrow(() -> new NotFoundException(NO_BUILDING_MAP));
		return ResponseEntity.ok(buildingMap.getData());
	}
}
