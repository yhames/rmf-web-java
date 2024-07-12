package com.rmf.apiserverjava.rmfapi.fleet;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RobotStateApi {
	private String name;
	// A simple token representing the status of the robot
	private Status2 status;
	// The ID of the task this robot is currently working on. Empty string if the robot is not working on a task.
	@JsonProperty("task_id")
	private String taskId;
	@JsonProperty("unix_millis_time")
	private Long unixMillisTime;
	private Location2DApi location;
	// State of charge of the battery. Values range from 0.0 (depleted) to 1.0 (fully charged)
	private Float battery;
	// A list of issues with the robot that operators need to address
	private List<Issue> issues;

	public RobotStateApi(String name, Status2 status, String taskId, Long unixMillisTime, Location2DApi location,
		Float battery, List<Issue> issues) {
		this.name = name;
		this.status = status;
		this.taskId = taskId;
		this.unixMillisTime = unixMillisTime;
		this.location = location;
		this.battery = battery;
		this.issues = issues;
	}

	@Getter
	@RequiredArgsConstructor
	public enum Status2 {
		uninitialized("uninitialized"),
		offline("offline"),
		shutdown("shutdown"),
		idle("idle"),
		charging("charging"),
		working("working"),
		error("error");
		private final String value;
	}

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Issue {
		// Category of the robot's issue
		String category;
		// Detailed information about the issue Union[Dict[str, Any], List, str]
		private JsonNode detail;

		public Issue(String category, JsonNode detail) {
			this.category = category;
			this.detail = detail;
		}
	}
}
