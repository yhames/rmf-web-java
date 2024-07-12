package com.rmf.apiserverjava.rosmsgs.buildingmap;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LevelMsg {
	// name: str = ""  # string
	// elevation: float = 0  # float32
	// images: List[AffineImage] = []  # rmf_building_map_msgs/AffineImage
	// places: List[Place] = []  # rmf_building_map_msgs/Place
	// doors: List[Door] = []  # rmf_building_map_msgs/Door
	// nav_graphs: List[Graph] = []  # rmf_building_map_msgs/Graph
	// wall_graph: Graph = Graph()  # rmf_building_map_msgs/Graph

	@JsonProperty("name")
	private String name;

	@JsonProperty("elevation")
	private float elevation;

	@JsonProperty("images")
	private List<AffineImageMsg> images;

	@JsonProperty("places")
	private List<PlaceMsg> places;

	@JsonProperty("doors")
	private List<DoorMsg> doors;

	@JsonProperty("nav_graphs")
	private List<GraphMsg> navGraphs;

	@JsonProperty("wall_graph")
	private GraphMsg wallGraph;
}
