package com.rmf.apiserverjava.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rmf.apiserverjava.dto.fleets.GetFleetLogsDto;
import com.rmf.apiserverjava.entity.fleets.FleetLog;
import com.rmf.apiserverjava.entity.fleets.FleetLogLog;
import com.rmf.apiserverjava.entity.fleets.FleetLogRobots;
import com.rmf.apiserverjava.entity.fleets.FleetState;
import com.rmf.apiserverjava.repository.FleetLogLogRepository;
import com.rmf.apiserverjava.repository.FleetLogRepository;
import com.rmf.apiserverjava.repository.FleetLogRobotRepository;
import com.rmf.apiserverjava.repository.FleetStateRepository;
import com.rmf.apiserverjava.rmfapi.fleet.FleetLogApi;
import com.rmf.apiserverjava.rmfapi.fleet.FleetStateApi;
import com.rmf.apiserverjava.service.FleetService;

import lombok.RequiredArgsConstructor;

/**
 * FleetServiceImpl.
 *
 * <p>
 *	FleetService의 구현체.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class FleetServiceImpl implements FleetService {
	private final FleetStateRepository fleetStateRepository;
	private final FleetLogRepository fleetLogRepository;
	private final FleetLogLogRepository fleetLogLogRepository;
	private final FleetLogRobotRepository fleetLogRobotsRepository;

	/**
	 * 모든 FleetState를 조회한다.
	 */
	@Override
	public List<FleetStateApi> getAllFleets() {
		return fleetStateRepository.findAll().stream().map(FleetState::getData).toList();
	}

	/**
	 * PK에 해당하는 FleetState를 조회한다.
	 */
	@Override
	public Optional<FleetStateApi> getFleetState(String fleetStateName) {
		return fleetStateRepository.findById(fleetStateName).map(FleetState::getData);
	}

	/**
	 * FleetLog에 PK에 해당하는 데이터가 없으면 Optional.empty()를 반환한다.
	 * 존재할 경우 FleetLogLog와 FleetLogRobots의 로그를 조회하여 반환한다.
	 */
	@Override
	public Optional<FleetLogApi> getFleetLog(GetFleetLogsDto logDto) {
		String id = logDto.getFleetStateName();
		long start = logDto.getTimeRange().getStartTimeMillis();
		long end = logDto.getTimeRange().getEndTimeMillis();

		Optional<FleetLog> fleetLog = fleetLogRepository.findByIdFetchRobots(id);
		if (!fleetLog.isPresent()) {
			return Optional.empty();
		}

		List<FleetLogLog> logs;
		List<FleetLogRobots> robots;
		logs = fleetLogLogRepository.findFleetLogLogByFleetLogIdAndTimeRangeFetch(id, start, end);
		robots = fleetLogRobotsRepository.findFleetLogRobotsAndLogsByFleetLogIdAndTimeRangeFetch(id, start, end);
		return Optional.of(FleetLogApi.builder().fleetLog(fleetLog.get()).log(logs).robots(robots).build());
	}
}
