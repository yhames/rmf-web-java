package com.rmf.apiserverjava.rosmsgs.buildingmap;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiftMsg {
	// name: str = ""  # string
	// levels: List[str] = []  # string
	// doors: List[Door] = []  # rmf_building_map_msgs/Door
	// wall_graph: Graph = Graph()  # rmf_building_map_msgs/Graph
	// ref_x: float = 0  # float32
	// ref_y: float = 0  # float32
	// ref_yaw: float = 0  # float32
	// width: float = 0  # float32
	// depth: float = 0  # float32

	@JsonProperty("name")
	private String name;

	@JsonProperty("levels")
	private List<String> levels;

	@JsonProperty("doors")
	private List<DoorMsg> doors;

	@JsonProperty("wall_graph")
	private GraphMsg wallGraph;

	// float32
	@JsonProperty("ref_x")
	private float refX;

	// float32
	@JsonProperty("ref_y")
	private float refY;

	// float32
	@JsonProperty("ref_yaw")
	private float refYaw;

	// float32
	@JsonProperty("width")
	private float width;

	// float32
	@JsonProperty("depth")
	private float depth;
}
