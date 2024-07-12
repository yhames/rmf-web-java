package com.rmf.apiserverjava.controller.impl;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rmf.apiserverjava.controller.TaskRestController;
import com.rmf.apiserverjava.dto.page.PaginationDto;
import com.rmf.apiserverjava.dto.tasks.TaskLogRequestDto;
import com.rmf.apiserverjava.dto.tasks.TaskStatesQueryRequestDto;
import com.rmf.apiserverjava.dto.time.TimeRangeDto;
import com.rmf.apiserverjava.global.exception.custom.NotFoundException;
import com.rmf.apiserverjava.global.parser.LogBetweenParser;
import com.rmf.apiserverjava.rmfapi.tasks.TaskEventLogApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskRequestApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskStateApi;
import com.rmf.apiserverjava.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Tasks")
@Controller
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskRestControllerImpl implements TaskRestController {

	private final TaskService taskService;

	private final LogBetweenParser logBetweenParser;

	private static final String NOT_FOUND = "Not found";
	private static final String DEFAULT_BETWEEN = "-60000";

	@Override
	@Operation(summary = "Get task request", description = "해당 task에 대한 request를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "taskReqeust 조회 성공"),
		@ApiResponse(responseCode = "404", description = "taskReqeust 조회 실패")
	})
	@GetMapping("/{taskId}/request")
	public ResponseEntity<TaskRequestApi> getTaskRequest(@PathVariable String taskId) {
		TaskRequestApi taskRequestApi = taskService.getTaskRequest(taskId)
			.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, taskId)));
		return ResponseEntity.ok(taskRequestApi);
	}

	@Override
	@Operation(summary = "Get taskState list", description = "taskState 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "taskState 목록 조회 성공"),
	})
	@GetMapping("")
	public List<TaskStateApi> queryTaskState(
		@RequestParam(name = "task_id", required = false) String taskId,
		@RequestParam(name = "category", required = false) String category,
		@RequestParam(name = "assigned_to", required = false) String assignedTo,
		@RequestParam(name = "start_time_between", required = false) String startTimeBetween,
		@RequestParam(name = "finish_time_between", required = false) String finishTimeBetween,
		@RequestParam(name = "status", required = false) String status,
		@RequestParam(name = "limit", required = false) Integer limit,
		@RequestParam(name = "offset", required = false) Integer offset,
		@RequestParam(name = "order_by", required = false) String orderBy) {

		TaskStatesQueryRequestDto taskStatesQueryRequestDto = TaskStatesQueryRequestDto.builder()
			.taskId(taskId)
			.category(category)
			.assignedTo(assignedTo)
			.startTimeBetween(startTimeBetween)
			.finishTimeBetween(finishTimeBetween)
			.status(status)
			.limit(limit)
			.offset(offset)
			.orderBy(orderBy)
			.build();
		return taskService.getTaskStateList(taskStatesQueryRequestDto);
	}

	@Override
	@Operation(summary = "Get task state", description = "해당 task에 대한 상태를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "taskState 조회 성공"),
		@ApiResponse(responseCode = "404", description = "taskState 조회 실패")
	})
	@GetMapping("/{taskId}/state")
	public ResponseEntity<TaskStateApi> getTaskState(@PathVariable String taskId) {
		TaskStateApi taskStateApi = taskService.getTaskState(taskId)
			.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, taskId)));
		return ResponseEntity.ok(taskStateApi);
	}

	@Override
	@Operation(summary = "Get task log", description = "해당 task에 대한 이벤트 로그를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "taskEventLog 조회 성공"),
		@ApiResponse(responseCode = "404", description = "taskEventLog 조회 실패")
	})
	@GetMapping("/{taskId}/log")
	public ResponseEntity<TaskEventLogApi> getTaskLog(@PathVariable String taskId,
		@RequestParam(required = false, defaultValue = DEFAULT_BETWEEN) String between) {

		TimeRangeDto timeRangeDto = logBetweenParser.parseBetween(between);
		TaskLogRequestDto taskLogRequestDto = TaskLogRequestDto.builder()
			.taskId(taskId)
			.timeRange(timeRangeDto)
			.build();
		TaskEventLogApi taskEventLogApi = taskService.getTaskLog(taskLogRequestDto)
			.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, taskId)));
		return ResponseEntity.ok(taskEventLogApi);
	}
}
