package com.rmf.apiserverjava.rosmsgs.buildingmap;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoorMsg {
	// name: str = ""  # string
	// v1_x: float = 0  # float32
	// v1_y: float = 0  # float32
	// v2_x: float = 0  # float32
	// v2_y: float = 0  # float32
	// door_type: pydantic.conint(ge=0, le=255) = 0  # uint8
	// motion_range: float = 0  # float32
	// motion_direction: pydantic.conint(ge=-2147483648, le=2147483647) = 0  # int32

	@JsonProperty("name")
	private String name;

	// float32
	@JsonProperty("v1_x")
	private float v1X;

	// float32
	@JsonProperty("v1_y")
	private float v1Y;

	// float32
	@JsonProperty("v2_x")
	private float v2X;

	// float32
	@JsonProperty("v2_y")
	private float v2Y;

	// uint8 (ge=0, le=255)
	@JsonProperty("door_type")
	private short doorType;

	// float32
	@JsonProperty("motion_range")
	private float motionRange;

	// int32 (ge=-2147483648, le=2147483647)
	@JsonProperty("motion_direction")
	private int motionDirection;
}
