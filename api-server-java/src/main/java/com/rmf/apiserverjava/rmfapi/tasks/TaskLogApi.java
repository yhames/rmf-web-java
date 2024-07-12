package com.rmf.apiserverjava.rmfapi.tasks;

import java.util.List;
import java.util.Map;

import com.rmf.apiserverjava.rmfapi.fleet.LogEntryApi;

import lombok.Builder;

public class TaskLogApi {

	private String taskId;
	private List<LogEntryApi> log;

	private Map<String, Phases> phases;

	@Builder
	public TaskLogApi(String taskId, List<LogEntryApi> log, Map<String, Phases> phases) {
		this.taskId = taskId;
		this.log = log;
		this.phases = phases;
	}

	private static class Phases {
		private List<LogEntryApi> log;
		private Map<String, LogEntryApi> event;
	}
}
