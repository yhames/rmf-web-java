package com.rmf.apiserverjava.rosmsgs.fleet;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RobotModeMsg {
	// uint32 (ge=0, le=4294967295)
	private int mode;
	// uint64 (ge=0, le=18446744073709551615)
	@JsonProperty("mode_request_id")
	private long modeRequestId;
}
