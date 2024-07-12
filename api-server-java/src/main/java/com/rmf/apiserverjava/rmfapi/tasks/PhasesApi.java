package com.rmf.apiserverjava.rmfapi.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhases;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhasesEvents;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhasesLog;
import com.rmf.apiserverjava.rmfapi.fleet.LogEntryApi;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhasesApi {

	private List<LogEntryApi> log;

	private Map<String, List<LogEntryApi>> events = new HashMap<>();

	@Builder
	public PhasesApi(TaskEventLogPhases phases,
		List<TaskEventLogPhasesLog> log, List<TaskEventLogPhasesEvents> events) {
		this.log = log.stream().map(LogEntryApi::new).toList();
		for (TaskEventLogPhasesEvents event : phases.getEvents()) {
			this.events.put(String.valueOf(event.getEvent()), new ArrayList<>());
		}
		for (TaskEventLogPhasesEvents event : events) {
			this.events.put(String.valueOf(event.getEvent()), event.getLogs().stream().map(LogEntryApi::new).toList());
		}
	}
}
