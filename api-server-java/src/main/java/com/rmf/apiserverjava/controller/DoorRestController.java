package com.rmf.apiserverjava.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.rmf.apiserverjava.dto.health.HealthResponseDto;
import com.rmf.apiserverjava.rosmsgs.door.DoorStateMsg;

/**
 * DoorRestController
 */
public interface DoorRestController {

	/**
	 * DoorState의 전체 조회를 수행한다.
	 * */
	ResponseEntity<List<DoorStateMsg>> getDoorStates();

	/**
	 * PK인 id를 기준으로 DoorState의 단일 조회를 수행한다.
	 * */
	ResponseEntity<DoorStateMsg> getDoorState(String doorName);

	/**
	 * PK인 id를 기준으로 DoorHealth의 단일 조회를 수행한다.
	 */
	ResponseEntity<HealthResponseDto> getDoorHealth(String doorName);
}
