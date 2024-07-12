package com.rmf.apiserverjava.rosmsgs.buildingmap;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AffineImageMsg {
	// name: str = ""  # string
	// x_offset: float = 0  # float32
	// y_offset: float = 0  # float32
	// yaw: float = 0  # float32
	// scale: float = 0  # float32
	// encoding: str = ""  # string
	// data: bytes = bytes()  # uint8

	@JsonProperty("name")
	private String name;

	// float32
	@JsonProperty("x_offset")
	private float xOffset;

	// float32
	@JsonProperty("y_offset")
	private float yOffset;

	// float32
	@JsonProperty("yaw")
	private float yaw;

	// float32
	@JsonProperty("scale")
	private float scale;

	@JsonProperty("encoding")
	private String encoding;

	// uint8 bytes()
	@JsonProperty("data")
	private String data;
}
