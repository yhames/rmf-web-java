package com.rmf.apiserverjava.rosmsgs.buildingmap;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GraphEdgeMsg {
	// v1_idx: pydantic.conint(ge=0, le=4294967295) = 0  # uint32
	// v2_idx: pydantic.conint(ge=0, le=4294967295) = 0  # uint32
	// params: List[Param] = []  # rmf_building_map_msgs/Param
	// edge_type: pydantic.conint(ge=0, le=255) = 0  # uint8

	// uint32 (ge=0, le=4294967295)
	@JsonProperty("v1_idx")
	private long v1Idx;

	// uint32 (ge=0, le=4294967295)
	@JsonProperty("v2_idx")
	private long v2Idx;

	@JsonProperty("params")
	private List<ParamMsg> params;

	// uint8 (ge=0, le=255)
	@JsonProperty("edge_type")
	private short edgeType;
}
