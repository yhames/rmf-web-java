package com.rmf.apiserverjava.rosmsgs.buildingmap;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceMsg {
	// name: str = ""  # string
	// x: float = 0  # float32
	// y: float = 0  # float32
	// yaw: float = 0  # float32
	// position_tolerance: float = 0  # float32
	// yaw_tolerance: float = 0  # float32

	@JsonProperty("name")
	private String name;

	// float32
	@JsonProperty("x")
	private float x;

	// float32
	@JsonProperty("y")
	private float y;

	// float32
	@JsonProperty("yaw")
	private float yaw;

	// float32
	@JsonProperty("position_tolerance")
	private float positionTolerance;

	// float32
	@JsonProperty("yaw_tolerance")
	private float yawTolerance;
}
