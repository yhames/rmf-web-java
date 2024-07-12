package com.rmf.apiserverjava.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import com.rmf.apiserverjava.dto.tasks.TaskStatesQueryRequestDto;
import com.rmf.apiserverjava.rmfapi.tasks.TaskEventLogApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskRequestApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskStateApi;

/**
 * Task와 관련된 REST API 엔드포인트를 정의한다.
 * */
public interface TaskRestController {

	/*
	 * TaskId를 기준으로 TaskRequest를 조회한다.
	 * */
	ResponseEntity<TaskRequestApi> getTaskRequest(String taskId);

	/*
	 * 쿼리파라미터를 기준으로 TaskState의 목록을 조회한다.
	 * */
	List<TaskStateApi> queryTaskState(String taskId,
		String category, String assignedTo,
		String startTimeBetween, String finishTimeBetween, String status,
		Integer limit, Integer offset, String orderBy);

	/*
	 * TaskId를 기준으로 TaskState를 조회한다.
	 * */
	ResponseEntity<TaskStateApi> getTaskState(String taskId);

	// /*
	// * TaskId와 쿼리파라미터의 시간을 기준으로 TaskEventLog를 조회한다.
	// * */
	ResponseEntity<TaskEventLogApi> getTaskLog(String taskId, String between);
}
