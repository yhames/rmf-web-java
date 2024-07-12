package com.rmf.apiserverjava.rmfapi.fleet;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FleetStateApi {
	private String name;
	// A dictionary of the states of the robots that belong to this fleet
	private Map<String, RobotStateApi> robots;

	public FleetStateApi(String name, Map<String, RobotStateApi> robots) {
		this.name = name;
		this.robots = robots;
	}
}
