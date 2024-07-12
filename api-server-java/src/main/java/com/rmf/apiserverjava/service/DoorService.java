package com.rmf.apiserverjava.service;

import java.util.List;
import java.util.Optional;

import com.rmf.apiserverjava.entity.doors.DoorHealth;
import com.rmf.apiserverjava.entity.doors.DoorState;

/**
 * DoorService
 *
 * <p>
 *     Door에 대한 비즈니스 로직을 수행한다.
 * </p>
 */
public interface DoorService {

	/**
	 * DoorState 전체 조회를 수행한다.
	 */
	List<DoorState> getDoorStates();

	/**
	 * DoorState 단일 조회를 수행한다.
	 */
	Optional<DoorState> getDoorState(String doorName);

	/**
	 * DoorHealth 단일 조회를 수행한다.
	 */
	Optional<DoorHealth> getDoorHealth(String doorName);

	/**
	 * DoorHealth를 업데이트하거나 생성한다.
	 */
	Optional<DoorHealth> updateOrCreateDoorHealth(DoorHealth doorHealth);

	/**
	 * DoorState를 업데이트하거나 생성한다.
	 */
	Optional<DoorState> updateOrCreateDoorState(DoorState doorState);
}
