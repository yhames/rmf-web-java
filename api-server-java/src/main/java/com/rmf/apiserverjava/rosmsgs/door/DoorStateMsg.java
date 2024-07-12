package com.rmf.apiserverjava.rosmsgs.door;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rmf.apiserverjava.entity.doors.DoorState;
import com.rmf.apiserverjava.rosmsgs.builtin.TimeMsg;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DoorStateMsg {
	// door_time: Time = Time()  # builtin_interfaces/Time
	// door_name: str = ""  # string
	// current_mode: DoorMode = DoorMode()  # rmf_door_msgs/DoorMode

	@JsonProperty("door_time")
	private TimeMsg doorTime;

	@JsonProperty("door_name")
	private String doorName;

	@JsonProperty("current_mode")
	private DoorModeMsg currentMode;

	@Builder
	public DoorStateMsg(TimeMsg doorTime, String doorName, DoorModeMsg currentMode) {
		this.doorTime = doorTime;
		this.doorName = doorName;
		this.currentMode = currentMode;
	}

	@Mapper
	public interface Mapstruct {

		DoorStateMsg.Mapstruct INSTANCE = Mappers.getMapper(DoorStateMsg.Mapstruct.class);

		@Mapping(target = "id", source = "doorName")
		@Mapping(target = "data", source = "doorStateMsg")
		DoorState toEntity(DoorStateMsg doorStateMsg);
	}
}
