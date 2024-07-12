package com.rmf.apiserverjava.rosmsgs.buildingmap;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GraphNodeMsg {
	// x: float = 0  # float32
	// y: float = 0  # float32
	// name: str = ""  # string
	// params: List[Param] = []  # rmf_building_map_msgs/Param

	// float32
	@JsonProperty("x")
	private float x;

	// float32
	@JsonProperty("y")
	private float y;

	@JsonProperty("name")
	private String name;

	@JsonProperty("params")
	private List<ParamMsg> params;
}
