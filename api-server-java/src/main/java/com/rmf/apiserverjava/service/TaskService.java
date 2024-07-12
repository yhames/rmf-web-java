package com.rmf.apiserverjava.service;

import java.util.List;
import java.util.Optional;

import com.rmf.apiserverjava.dto.page.PaginationDto;
import com.rmf.apiserverjava.dto.tasks.TaskLogRequestDto;
import com.rmf.apiserverjava.dto.tasks.TaskStatesQueryRequestDto;
import com.rmf.apiserverjava.rmfapi.tasks.TaskEventLogApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskRequestApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskStateApi;

/**
 * Task와 관련된 비즈니스 로직을 정의한다.
 * */
public interface TaskService {

	/*
	 * TaskId를 기준으로 TaskRequest를 조회한다.
	 * */
	public Optional<TaskRequestApi> getTaskRequest(String taskId);

	/*
	 * 쿼리파라미터를 기준으로 TaskState의 목록을 조회한다.
	 * */
	public List<TaskStateApi> getTaskStateList(TaskStatesQueryRequestDto taskStatesQueryRequestDto);

	/*
	 * TaskId를 기준으로 TaskState를 조회한다.
	 * */
	public Optional<TaskStateApi> getTaskState(String taskId);

	/*
	 * TaskId에 해당하는 로그를 시간 조건에 따라 조회한다.
	 * */

	public Optional<TaskEventLogApi> getTaskLog(TaskLogRequestDto taskLogRequestDto);

	/**
	* taskId로 task를 조회하고, taskState를 저장 혹은 업데이트한다.
	* */
	void updateOrCreateTaskState(TaskStateApi taskStateApi);

	/**
	* TaskEventLog를 저장한다. 연관된 데이터들도 한꺼번에 저장한다.
	* */
	void saveTaskLog(TaskEventLogApi taskEventLogApi);


	void postDispatchTask(TaskRequestApi taskRequest);

	/**
	 * Alerts ack 이벤트 발생시 관련 TaskState, TaskEventLog의 phase를 추가한다.
	 */
	void saveLogAcknowledgedTaskCompletion(String taskId, String acknowledgedBy, long ackTimeMills);
}
