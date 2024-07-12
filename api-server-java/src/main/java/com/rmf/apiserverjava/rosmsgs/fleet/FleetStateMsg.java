package com.rmf.apiserverjava.rosmsgs.fleet;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FleetStateMsg {
	private String name;
	private List<RobotStateMsg> robots;
}
