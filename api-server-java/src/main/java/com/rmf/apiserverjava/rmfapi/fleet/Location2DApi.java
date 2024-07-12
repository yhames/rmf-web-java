package com.rmf.apiserverjava.rmfapi.fleet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location2DApi {
	private String map;
	private double x;
	private double y;
	private double yaw;

	public Location2DApi(String map, double x, double y, double yaw) {
		this.map = map;
		this.x = x;
		this.y = y;
		this.yaw = yaw;
	}
}
