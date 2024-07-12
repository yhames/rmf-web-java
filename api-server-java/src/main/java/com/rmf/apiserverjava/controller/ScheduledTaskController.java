package com.rmf.apiserverjava.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.rmf.apiserverjava.dto.jwt.JwtUserInfoDto;
import com.rmf.apiserverjava.dto.scheduledtasks.CreateScheduledTaskRequestDto;
import com.rmf.apiserverjava.dto.scheduledtasks.ScheduledTaskPaginationDto;
import com.rmf.apiserverjava.dto.scheduledtasks.ScheduledTaskResponseDto;
import com.rmf.apiserverjava.dto.scheduledtasks.UpdateScheduledTaskRequestDto;

/**
 * ScheduledTaskController
 *
 * <p>
 *
 * </p>
 */
public interface ScheduledTaskController {

	List<ScheduledTaskResponseDto> getScheduledTasks(ScheduledTaskPaginationDto scheduledTaskRequest);

	ResponseEntity<ScheduledTaskResponseDto> postScheduledTask(
		JwtUserInfoDto jwtUserInfoDto,
		CreateScheduledTaskRequestDto createScheduledTaskRequestDto);

	ResponseEntity<ScheduledTaskResponseDto> getScheduledTask(int scheduledTaskId);

	ResponseEntity<String> deleteScheduledTask(int scheduledTaskId);

	ResponseEntity<ScheduledTaskResponseDto> updateScheduledTask(int scheduledTaskId,
		String exceptDate, UpdateScheduledTaskRequestDto createScheduledTaskRequestDto, JwtUserInfoDto jwtUserInfoDto);

	ResponseEntity<String> clearScheduledTaskEvent(int scheduledTaskId, String eventDate);
}
