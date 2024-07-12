package com.rmf.apiserverjava.service;

import java.util.List;
import java.util.Optional;

import com.rmf.apiserverjava.dto.fleets.GetFleetLogsDto;
import com.rmf.apiserverjava.rmfapi.fleet.FleetLogApi;
import com.rmf.apiserverjava.rmfapi.fleet.FleetStateApi;

/**
 * FleetService.
 *
 * <p>
 *	Fleet에 대한 비즈니스 로직을 정의한다.
 * </p>
 */
public interface FleetService {
	/**
	 * 모든 FleetState를 조회한다.
	 */
	List<FleetStateApi> getAllFleets();

	/**
	 * FleetState에 PK에 해당하는 FleetState를 조회한다.
	 */
	Optional<FleetStateApi> getFleetState(String fleetStateName);

	/**
	 * FleetLog에 PK에 해당하는 FleetLog의 로그를 시간 조건에 따라 조회한다.
	 */
	Optional<FleetLogApi> getFleetLog(GetFleetLogsDto logDto);

}
