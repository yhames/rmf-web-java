package com.rmf.apiserverjava.controller.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rmf.apiserverjava.controller.TaskFavoriteController;
import com.rmf.apiserverjava.dto.jwt.JwtUserInfoDto;
import com.rmf.apiserverjava.dto.tasks.TaskFavoriteDto;
import com.rmf.apiserverjava.entity.tasks.TaskFavorite;
import com.rmf.apiserverjava.global.annotation.jwt.JwtUserInfo;
import com.rmf.apiserverjava.global.exception.hadler.GlobalExceptionHandlerAdvice;
import com.rmf.apiserverjava.service.TaskFavoriteService;

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
 * TaskFavoriteControllerImpl.
 *
 * <p>
 *	TaskFavoriteController의 구현체
 * </p>
 */
@Tag(name = "TaskFavorite")
@RestController
@RequestMapping("/favorite_tasks")
@RequiredArgsConstructor
public class TaskFavoriteControllerImpl implements TaskFavoriteController {
	private final TaskFavoriteService taskFavoriteService;

	@Override
	@Operation(summary = "Get FavoritesTasks", description = "Id에 해당하는 FavoritesTasks 전체 조회를 요청합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "FavoritesTasks 조회 성공"),
	})
	@GetMapping
	public ResponseEntity<List<TaskFavoriteDto>> getFavoritesTasks(
		@Parameter(hidden = true) @JwtUserInfo JwtUserInfoDto jwtUserInfoDto) {

		List<TaskFavorite> taskFavorites = taskFavoriteService.getFavoritesTasks(jwtUserInfoDto.getUsername());
		List<TaskFavoriteDto> taskFavoritesDto = taskFavorites.stream()
			.map(TaskFavoriteDto.MapStruct.INSTANCE::toDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(taskFavoritesDto);
	}

	@Override
	@Operation(summary = "Post FavoritesTasks", description = "새로운 FavoritesTasks를 생성합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "FavoritesTasks 생성/업데이트에 성공합니다."),
		@ApiResponse(responseCode = "403", description = "본인의 FavoritesTasks가 아니어서 수정할 권한이 없습니다."),
		@ApiResponse(responseCode = "404", description = "전달한 ID의 해당하는 FavoritesTasks가 존재하지 않아 업데이트에 실패합니다"),
	})
	@PostMapping
	public ResponseEntity<TaskFavoriteDto> postFavoriteTask(
		@RequestBody @Valid TaskFavoriteDto taskFavoriteDto,
		@Parameter(hidden = true) @JwtUserInfo JwtUserInfoDto jwtUserInfoDto) {
		taskFavoriteDto.setUser(jwtUserInfoDto.getUsername());
		TaskFavorite taskFavorite = taskFavoriteService.postFavoriteTask(taskFavoriteDto);
		TaskFavoriteDto taskFavoriteDtoResponse = TaskFavoriteDto.MapStruct.INSTANCE.toDto(taskFavorite);
		return ResponseEntity.ok(taskFavoriteDtoResponse);
	}

	@Override
	@Operation(summary = "DELETE FavoritesTasks", description = "ID를 통해 FavoritesTasks를 삭제합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "FavoritesTasks 삭제에 성공합니다."),
		@ApiResponse(responseCode = "404", description = "FavoritesTasks가 존재하지 않습니다.",
			content = @Content(schema = @Schema(implementation = GlobalExceptionHandlerAdvice.ErrorResponse.class))),
		@ApiResponse(responseCode = "403", description = "FavoritesTasks를 삭제할 권한이 없습니다.",
			content = @Content(schema = @Schema(implementation = GlobalExceptionHandlerAdvice.ErrorResponse.class))),
	})
	@DeleteMapping("/{favoriteTaskId}")
	public ResponseEntity<Void> deleteFavoriteTask(
		@PathVariable String favoriteTaskId,
		@Parameter(hidden = true) @JwtUserInfo JwtUserInfoDto jwtUserInfoDto) {

		taskFavoriteService.deleteFavoriteTask(favoriteTaskId, jwtUserInfoDto.getUsername());
		return ResponseEntity.ok().build();
	}
}
