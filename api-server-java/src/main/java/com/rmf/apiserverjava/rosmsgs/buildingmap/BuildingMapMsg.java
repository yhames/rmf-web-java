package com.rmf.apiserverjava.rosmsgs.buildingmap;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rmf.apiserverjava.entity.buildingmaps.BuildingMap;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BuildingMapMsg {
	// name: str = ""  # string
	// levels: List[Level] = []  # rmf_building_map_msgs/Level
	// lifts: List[Lift] = []  # rmf_building_map_msgs/Lift

	@JsonProperty("name")
	private String name;

	@JsonProperty("levels")
	private List<LevelMsg> levels;

	@JsonProperty("lifts")
	private List<LiftMsg> lifts;

	@Builder
	public BuildingMapMsg(String name, List<LevelMsg> levels, List<LiftMsg> lifts) {
		this.name = name;
		this.levels = levels;
		this.lifts = lifts;
	}

	@Mapper
	public interface MapStruct {

		BuildingMapMsg.MapStruct INSTANCE = Mappers.getMapper(BuildingMapMsg.MapStruct.class);

		@Mapping(target = "id", source = "name")
		@Mapping(target = "data", source = "buildingMapMsg")
		BuildingMap toEntity(BuildingMapMsg buildingMapMsg);
	}
}
