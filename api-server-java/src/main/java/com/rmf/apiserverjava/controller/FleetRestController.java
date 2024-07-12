package com.rmf.apiserverjava.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.rmf.apiserverjava.global.exception.custom.NotFoundException;
import com.rmf.apiserverjava.rmfapi.fleet.FleetLogApi;
import com.rmf.apiserverjava.rmfapi.fleet.FleetStateApi;

/**
 * FleetRestController.
 *
 * <p>
 *	Fleet에 대한 REST API 엔드포인트를 정의한다.
 * </p>
 */
public interface FleetRestController {
	/**
	 * 모든 FleetState를 조회한다.
	 */
	ResponseEntity<List<FleetStateApi>> getFleets();

	/**
	 * PK에 해당하는 FleetState를 조회한다.
	 *
	 * @throws NotFoundException
	 */
	ResponseEntity<FleetStateApi> getFleetState(String fleetStateName);


	/**
	 * PK에 해당하는 FleetState의 로그를 between 조건에 따라 조회한다.
	 *
	 * @throws NotFoundException
	 */
	ResponseEntity<FleetLogApi> getFleetLog(String fleetStateName, String between);
}
