package com.rmf.apiserverjava.rosmsgs.fleet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rmf.apiserverjava.rosmsgs.builtin.TimeMsg;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocationMsg {

	private TimeMsg t;
	private float x;
	private float y;
	private float yaw;
	@JsonProperty("obey_approach_speed_limit")
	private boolean obeyApproachSpeedLimit;
	@JsonProperty("approach_speed_limit")
	private float approachSpeedLimit;
	@JsonProperty("level_name")
	private String levelName;
	// uint64 (ge=0, le=18446744073709551615)
	private long index;
}
