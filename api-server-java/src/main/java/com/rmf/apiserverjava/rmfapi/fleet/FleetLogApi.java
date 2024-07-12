package com.rmf.apiserverjava.rmfapi.fleet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rmf.apiserverjava.entity.fleets.FleetLog;
import com.rmf.apiserverjava.entity.fleets.FleetLogLog;
import com.rmf.apiserverjava.entity.fleets.FleetLogRobots;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FleetLogApi {
	private String name;
	// Log for the overall fleet
	private List<LogEntryApi> log;
	// Dictionary of logs for the individual robots. The keys (property names) are the robot names.
	private Map<String, List<LogEntryApi>> robots = new HashMap<>();

	public FleetLogApi(String name, List<LogEntryApi> log, Map<String, List<LogEntryApi>> robots) {
		this.name = name;
		this.log = log;
		this.robots = robots;
	}

	@Builder
	public FleetLogApi(FleetLog fleetLog, List<FleetLogLog> log, List<FleetLogRobots> robots) {
		this.name = fleetLog.getName();
		this.log = log.stream().map(LogEntryApi::new).toList();
		for (FleetLogRobots robot : fleetLog.getRobots()) {
			this.robots.put(robot.getName(), new ArrayList<>());
		}
		for (FleetLogRobots robot : robots) {
			this.robots.put(robot.getName(), robot.getLogs().stream().map(LogEntryApi::new).toList());
		}
	}
}
