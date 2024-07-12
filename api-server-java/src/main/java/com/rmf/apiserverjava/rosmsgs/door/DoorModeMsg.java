package com.rmf.apiserverjava.rosmsgs.door;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rmf.apiserverjava.entity.doors.DoorHealth;
import com.rmf.apiserverjava.entity.doors.DoorState;
import com.rmf.apiserverjava.entity.health.HealthStatus;

import lombok.RequiredArgsConstructor;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@RequiredArgsConstructor
public enum DoorModeMsg {
	MODE_CLOSED(0),
	MODE_MOVING(1),
	MODE_OPEN(2),
	MODE_OFFLINE(3),
	MODE_UNKNOWN(4);

	private final int value;

	public int getValue() {
		return this.value;
	}

	@JsonCreator
	public static DoorModeMsg fromValue(@JsonProperty("value") int value) {
		for (DoorModeMsg mode : DoorModeMsg.values()) {
			if (mode.getValue() == value) {
				return mode;
			}
		}
		return MODE_UNKNOWN;
	}

	public static DoorHealth toDoorHealth(DoorState doorState) {
		String doorName = doorState.getId();
		DoorModeMsg currentMode = doorState.getData().getCurrentMode();
		if (currentMode == DoorModeMsg.MODE_OPEN
			|| currentMode == DoorModeMsg.MODE_MOVING
			|| currentMode == DoorModeMsg.MODE_CLOSED) {
			return DoorHealth.builder()
				.id(doorName)
				.healthStatus(HealthStatus.Healthy)
				.healthMessage("")
				.build();
		} else if (currentMode == DoorModeMsg.MODE_OFFLINE) {
			return DoorHealth.builder()
				.id(doorName)
				.healthStatus(HealthStatus.Unhealthy)
				.healthMessage("Door is offline")
				.build();
		}
		return DoorHealth.builder()
			.id(doorName)
			.healthStatus(HealthStatus.Unhealthy)
			.healthMessage("door is in an unknown mode")
			.build();
	}
}
