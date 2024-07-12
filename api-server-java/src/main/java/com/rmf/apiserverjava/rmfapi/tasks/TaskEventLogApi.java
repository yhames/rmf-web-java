package com.rmf.apiserverjava.rmfapi.tasks;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rmf.apiserverjava.entity.tasks.TaskEventLog;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogLog;
import com.rmf.apiserverjava.rmfapi.fleet.LogEntryApi;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskEventLogApi {

	@JsonProperty("task_id")
	private String taskId;

	private List<LogEntryApi> log;

	private Map<String, PhasesApi> phases;

	@Builder
	public TaskEventLogApi(TaskEventLog taskEventLog, List<TaskEventLogLog> log, Map<String, PhasesApi> phases) {
		this.taskId = taskEventLog.getTaskId();
		this.log = log.stream().map(LogEntryApi::new).toList();
		this.phases = phases;
	}
}
