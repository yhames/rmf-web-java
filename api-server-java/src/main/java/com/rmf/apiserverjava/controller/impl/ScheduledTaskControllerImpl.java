package com.rmf.apiserverjava.controller.impl;

import static com.rmf.apiserverjava.global.exception.hadler.GlobalExceptionHandlerAdvice.*;

import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rmf.apiserverjava.controller.ScheduledTaskController;
import com.rmf.apiserverjava.dto.jwt.JwtUserInfoDto;
import com.rmf.apiserverjava.dto.scheduledtasks.CreateScheduledTaskDto;
import com.rmf.apiserverjava.dto.scheduledtasks.CreateScheduledTaskRequestDto;
import com.rmf.apiserverjava.dto.scheduledtasks.ScheduledTaskPaginationDto;
import com.rmf.apiserverjava.dto.scheduledtasks.ScheduledTaskResponseDto;
import com.rmf.apiserverjava.dto.scheduledtasks.ScheduledTaskSearchCondition;
import com.rmf.apiserverjava.dto.scheduledtasks.UpdateScheduledTaskDto;
import com.rmf.apiserverjava.dto.scheduledtasks.UpdateScheduledTaskRequestDto;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTask;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.annotation.jwt.JwtUserInfo;
import com.rmf.apiserverjava.global.exception.custom.BusinessException;
import com.rmf.apiserverjava.global.exception.custom.DateTimeParseException;
import com.rmf.apiserverjava.global.exception.custom.NotFoundException;
import com.rmf.apiserverjava.service.ScheduledTaskService;
import com.rmf.apiserverjava.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ScheduledTaskControllerImpl
 *
 * <p>
 *     ScheduledTaskController의 구현체
 * </p>
 */
@Slf4j
@Tag(name = "Tasks")
@RestController
@RequestMapping("/scheduled_tasks")
@RequiredArgsConstructor
public class ScheduledTaskControllerImpl implements ScheduledTaskController {

	public static final String CREATE_SCHEDULED_FAILED = "Failed to create ScheduledTask";

	public static final String SCHEDULED_TASK_NOT_FOUND = "ScheduledTask not found";

	public static final String INVALID_ISO_FORMAT = "올바른 ISO-8601 날짜 형식이 아닙니다: ";

	private final ScheduledTaskService scheduledTaskService;

	private final UserService userService;

	@Override
	@Operation(summary = "Get ScheduledTasks", description = "ScheduledTasks 전체 조회를 요청합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "ScheduledTasks 조회 성공")
	})
	@GetMapping
	public List<ScheduledTaskResponseDto> getScheduledTasks(
		@ModelAttribute ScheduledTaskPaginationDto scheduledTaskPaginationDto) {
		ScheduledTaskSearchCondition condition = ScheduledTaskSearchCondition.MapStruct.INSTANCE
			.toDto(scheduledTaskPaginationDto);
		return scheduledTaskService.getScheduledTasks(condition).stream()
			.map(ScheduledTaskResponseDto.MapStruct.INSTANCE::toDto)
			.toList();
	}

	@Override
	@Operation(summary = "Post ScheduledTask", description = "ScheduledTask를 저장하고 스케쥴러에 등록합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "ScheduledTask 저장 성공"),
		@ApiResponse(responseCode = "400", description = "ScheduledTask Validation 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PostMapping
	public ResponseEntity<ScheduledTaskResponseDto> postScheduledTask(
		@Parameter(hidden = true) @JwtUserInfo JwtUserInfoDto jwtUserInfoDto,
		@RequestBody CreateScheduledTaskRequestDto createScheduledTaskRequestDto) {
		CreateScheduledTaskDto createScheduledTaskDto = CreateScheduledTaskDto.MapStruct.INSTANCE
			.toDto(createScheduledTaskRequestDto);
		User user = userService.getUser(jwtUserInfoDto.getUsername())
			.orElseThrow(() -> new NotFoundException("User not found"));
		ScheduledTask scheduledTask = scheduledTaskService.postScheduledTask(createScheduledTaskDto, user)
			.orElseThrow(() -> new BusinessException(CREATE_SCHEDULED_FAILED));
		return ResponseEntity.ok(ScheduledTaskResponseDto.MapStruct.INSTANCE.toDto(scheduledTask));
	}

	@Override
	@Operation(summary = "Get ScheduledTask", description = "ScheduledTask 단건 조회를 요청합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "ScheduledTask 조회 성공"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 ScheduledTask",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@GetMapping("/{task_id}")
	public ResponseEntity<ScheduledTaskResponseDto> getScheduledTask(@PathVariable("task_id") int scheduledTaskId) {
		ScheduledTask scheduledTask = scheduledTaskService.getScheduledTask(scheduledTaskId)
			.orElseThrow(() -> new NotFoundException(SCHEDULED_TASK_NOT_FOUND));
		return ResponseEntity.ok(ScheduledTaskResponseDto.MapStruct.INSTANCE.toDto(scheduledTask));
	}

	@Override
	@Operation(summary = "Delete ScheduledTask", description = "해당 ScheduledTask를 삭제하고 스케쥴러에서 제외합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "ScheduledTask 삭제 성공"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 ScheduledTask",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@DeleteMapping("/{task_id}")
	public ResponseEntity<String> deleteScheduledTask(@PathVariable("task_id") int scheduledTaskId) {
		scheduledTaskService.deleteScheduledTask(scheduledTaskId);
		return ResponseEntity.ok("success");
	}

	@Override
	@Operation(summary = "Update ScheduledTask", description = "해당 ScheduledTask를 업데이트하고 스케쥴러에 재등록합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "ScheduledTask 업데이트 성공"),
		@ApiResponse(responseCode = "400", description = "Validation 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 ScheduledTask",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@PostMapping("/{task_id}/update")
	public ResponseEntity<ScheduledTaskResponseDto> updateScheduledTask(
		@PathVariable("task_id") int scheduledTaskId,
		@RequestParam(value = "except_date", required = false) String exceptDate,
		@RequestBody UpdateScheduledTaskRequestDto updateScheduledTaskRequestDto,
		@Parameter(hidden = true) @JwtUserInfo JwtUserInfoDto jwtUserInfoDto) {
		if (exceptDate != null && isNotIsoFormat(exceptDate)) {
			throw new DateTimeParseException(INVALID_ISO_FORMAT + exceptDate);
		}
		User user = userService.getUser(jwtUserInfoDto.getUsername())
			.orElseThrow(() -> new NotFoundException("User not found"));
		UpdateScheduledTaskDto updateScheduledTaskDto = UpdateScheduledTaskDto.MapStruct.INSTANCE
			.toDto(updateScheduledTaskRequestDto);
		ScheduledTask scheduledTask = exceptDate == null
			? scheduledTaskService.updateScheduledTask(scheduledTaskId, updateScheduledTaskDto)
			: scheduledTaskService.updateScheduledTaskSingleEvent(
				scheduledTaskId, exceptDate, updateScheduledTaskDto, user);
		return ResponseEntity.ok(ScheduledTaskResponseDto.MapStruct.INSTANCE.toDto(scheduledTask));
	}

	@Override
	@Operation(summary = "Clear ScheduledTask",
		description = "event_date에 해당하는 날짜를 스케쥴러에서 제외합니다. event_date는 ISO-8601 형식의 날짜 문자열 입니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "ScheduledTask Clear 성공"),
		@ApiResponse(responseCode = "400", description = "Validation 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 ScheduledTask",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@PutMapping("/{task_id}/clear")
	public ResponseEntity<String> clearScheduledTaskEvent(@PathVariable("task_id") int scheduledTaskId,
		@RequestParam("event_date") String eventDate) {
		if (isNotIsoFormat(eventDate)) {
			throw new DateTimeParseException(INVALID_ISO_FORMAT + eventDate);
		}
		scheduledTaskService.clearScheduledTaskEvent(scheduledTaskId, eventDate);
		return ResponseEntity.ok("success");
	}

	private boolean isNotIsoFormat(String format) {
		try {
			DateTimeFormatter.ISO_DATE_TIME.parse(format);
			return false;
		} catch (DateTimeException e) {
			return true;
		}
	}
}
