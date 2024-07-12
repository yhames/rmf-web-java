package com.rmf.apiserverjava.rosmsgs.fleet;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RobotStateMsg {
	private String name;
	private String model;
	@JsonProperty("task_id")
	private String taskId;
	// uint64 (ge=0, le=18446744073709551615)
	private long seq;
	private RobotModeMsg mode;
	@JsonProperty("battery_percent")
	private float batteryPercent;
	private LocationMsg location;
	private List<LocationMsg> path;
}
