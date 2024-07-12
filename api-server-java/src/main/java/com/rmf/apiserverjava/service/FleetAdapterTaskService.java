package com.rmf.apiserverjava.service;

import com.rmf.apiserverjava.rmfapi.tasks.TaskEventLogApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskStateApi;

/**
 * FleetAdapterTaskService.
 *
 * <p>
 *	FleetAdapter에서 전달하는 Task_state_update, Task_log_update 메시지를 처리하는 서비스.
 * </p>
 */

public interface FleetAdapterTaskService {

	/**
	 * taskState를 저장하거나, 업데이트한다.
	 * 관련된 TaskStateEvent를 발행한다.
	 * taskState가 완료되었을 경우, AlertEvent를 발행한다.
	 * */
	void saveOrUpdateTaskState(TaskStateApi taskStateApi);

	/**
	* TaskEventLog를 저장한다.
	 * 관련된 TaskEventLogEvent를 발행한다.
	 * TaskEventLog에 에러가 있을 경우, AlertEvent를 발행한다.
	* */
	void saveTaskEventLog(TaskEventLogApi taskEventLogApi);
}
