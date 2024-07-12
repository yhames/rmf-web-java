package com.rmf.apiserverjava.rosmsgs.lift;

import java.util.List;

import com.rmf.apiserverjava.rosmsgs.builtin.TimeMsg;

public class LiftStateMsg {
	// lift_time: Time = Time()  # builtin_interfaces/Time
	// lift_name: str = ""  # string
	// available_floors: List[str] = []  # string
	// current_floor: str = ""  # string
	// destination_floor: str = ""  # string
	// door_state: pydantic.conint(ge=0, le=255) = 0  # uint8
	// motion_state: pydantic.conint(ge=0, le=255) = 0  # uint8
	// available_modes: bytes = bytes()  # uint8
	// current_mode: pydantic.conint(ge=0, le=255) = 0  # uint8
	// session_id: str = ""  # string

	private TimeMsg liftTime;

	private String liftName;

	private List<String> availableFloors;

	private String currentFloor;

	private String destinationFloor;

	// uint8 (ge=0, le=255)
	private short doorState;

	// uint8 (ge=0, le=255)
	private short motionState;

	// uint8 bytes()
	private short availableModes;

	// uint8 (ge=0, le=255)
	private short currentMode;

	private String sessionId;
}
