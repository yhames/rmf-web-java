package com.rmf.apiserverjava.rosmsgs.buildingmap;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GraphMsg {
	// name: str = ""  # string
	// vertices: List[GraphNode] = []  # rmf_building_map_msgs/GraphNode
	// edges: List[GraphEdge] = []  # rmf_building_map_msgs/GraphEdge
	// params: List[Param] = []  # rmf_building_map_msgs/Param

	@JsonProperty("name")
	private String name;

	@JsonProperty("vertices")
	private List<GraphNodeMsg> vertices;

	@JsonProperty("edges")
	private List<GraphEdgeMsg> edges;

	@JsonProperty("params")
	private List<ParamMsg> params;
}
