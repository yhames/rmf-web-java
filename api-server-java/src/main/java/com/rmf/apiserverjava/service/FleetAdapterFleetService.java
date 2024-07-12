package com.rmf.apiserverjava.service;

import com.rmf.apiserverjava.rmfapi.fleet.FleetLogApi;
import com.rmf.apiserverjava.rmfapi.fleet.FleetStateApi;

/**
 * FleetAdapterFleetService.
 *
 * <p>
 *	FleetAdapter에서 전달하는 FleetStateApi, FleetLogApi를 저장하는 서비스.
 * </p>
 */
public interface FleetAdapterFleetService {
	/**
	 * FleetState를 저장하거나 업데이트한다.
	 */
	void saveOrUpdateFleetState(FleetStateApi fleetStateApi);

	/**
	 * FleetLog 및 연관된 로그를 저장한다.
	 */
	void saveFleetLog(FleetLogApi fleetLogApi);
}
