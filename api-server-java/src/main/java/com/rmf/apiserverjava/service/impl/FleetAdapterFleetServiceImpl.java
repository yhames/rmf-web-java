package com.rmf.apiserverjava.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rmf.apiserverjava.entity.fleets.FleetLog;
import com.rmf.apiserverjava.entity.fleets.FleetLogLog;
import com.rmf.apiserverjava.entity.fleets.FleetLogRobots;
import com.rmf.apiserverjava.entity.fleets.FleetLogRobotsLog;
import com.rmf.apiserverjava.entity.fleets.FleetState;
import com.rmf.apiserverjava.repository.FleetLogLogRepository;
import com.rmf.apiserverjava.repository.FleetLogRepository;
import com.rmf.apiserverjava.repository.FleetLogRobotRepository;
import com.rmf.apiserverjava.repository.FleetLogRobotsLogRepository;
import com.rmf.apiserverjava.repository.FleetStateRepository;
import com.rmf.apiserverjava.rmfapi.fleet.FleetLogApi;
import com.rmf.apiserverjava.rmfapi.fleet.FleetStateApi;
import com.rmf.apiserverjava.rmfapi.fleet.LogEntryApi;
import com.rmf.apiserverjava.rxjava.eventbus.FleetEvents;
import com.rmf.apiserverjava.service.FleetAdapterFleetService;

import lombok.RequiredArgsConstructor;

/**
 * FleetAdapterFleetServiceImpl.
 *
 * <p>
 *	FleetState 및 FleetLog 데이터를 저장하는 서비스
 * </p>
 */
@Service
@RequiredArgsConstructor
public class FleetAdapterFleetServiceImpl implements FleetAdapterFleetService {
	private final FleetStateRepository fleetStateRepository;
	private final FleetLogRepository fleetLogRepository;
	private final FleetLogLogRepository fleetLogLogRepository;
	private final FleetLogRobotRepository fleetLogRobotRepository;
	private final FleetLogRobotsLogRepository fleetLogRobotLogRepository;
	private final FleetEvents fleetEvents;

	/**
	 * FleetStateApi 데이터를 통해 FleetState를 업데이트하거나 새로 생성한다.
	 */
	@Override
	@Transactional
	public void saveOrUpdateFleetState(FleetStateApi fleetStateApi) {
		Optional<FleetState> fleetState = fleetStateRepository.findById(fleetStateApi.getName());

		if (fleetState.isPresent()) {
			fleetState.get().updateData(fleetStateApi);
		} else {
			fleetStateRepository.save(FleetState.builder().name(fleetStateApi.getName()).data(fleetStateApi).build());
		}

		fleetEvents.getFleetStatesEvent().onNext(fleetStateApi);
	}

	/**
	 * FleetLog 및 관련된 로봇 및 로그 데이터를 저장한다. 이후 FleetEventLogsEvent를 발행한다.
	 */
	@Override
	@Transactional
	public void saveFleetLog(FleetLogApi fleetLogApi) {
		FleetLog fleetLog = getOrCreateFleetLog(fleetLogApi.getName());
		List<LogEntryApi> newLogs = getNewLogs(fleetLogApi);
		saveFleetLogLogs(fleetLog, newLogs);
		saveFleetRobotsWithLogs(fleetLog, fleetLogApi);
		fleetEvents.getFleetEventLogsEvent().onNext(fleetLogApi);
	}

	/**
	 * FleetLogLog 데이터를 벌크 저장한다.
	 * DB에 존재하는 fleetLog의 모든 자식 FleetLogLog 조회하는 것을 방지하기 위해 연관관계 동기화를 진행하지 않는다.
	 */
	@Transactional
	public void saveFleetLogLogs(FleetLog fleetLog, List<LogEntryApi> logs) {
		List<FleetLogLog> fleetLogLogs = logs.stream()
			.map(LogEntryApi.MapStruct.INSTANCE::toFleetLogLogEntity)
			.map((log) -> log.setFleetLogWithoutRelation(fleetLog))
			.collect(Collectors.toList());
		fleetLogLogRepository.saveAll(fleetLogLogs);
	}

	/**
	 * FleetLogLog의 유니크 인덱스인 (fleet, seq) 조합을 활용해 새로운 로그를 필터링한다.
	 */
	public List<LogEntryApi> getNewLogs(FleetLogApi fleetLogApi) {
		List<Integer> seqList = fleetLogApi.getLog().stream().map(LogEntryApi::getSeq).collect(Collectors.toList());
		List<Integer> existingSeq = fleetLogLogRepository.findSeqByFleetIdAndSeqIn(fleetLogApi.getName(), seqList);
		return fleetLogApi.getLog()
			.stream()
			.filter(log -> !existingSeq.contains(log.getSeq()))
			.collect(Collectors.toList());
	}

	/**
	 * FleetLog 반환하며 존재하지 않는다면 새로 생성한다.
	 */
	@Transactional
	public FleetLog getOrCreateFleetLog(String name) {
		Optional<FleetLog> fleetLog = fleetLogRepository.findById(name);
		if (fleetLog.isPresent()) {
			return fleetLog.get();
		} else {
			FleetLog newFleetLog = FleetLog.builder().name(name).build();
			return fleetLogRepository.save(newFleetLog);
		}
	}

	/**
	 * FleetLogRobots 및 로그 데이터를 저장한다.
	 */
	@Transactional
	public void saveFleetRobotsWithLogs(FleetLog fleetLog, FleetLogApi fleetLogApi) {
		List<FleetLogRobots> newRobots = new ArrayList<>();
		for (String robotName : fleetLogApi.getRobots().keySet()) {
			Optional<FleetLogRobots> robot = fleetLogRobotRepository.findByFleetAndName(fleetLog, robotName);
			if (!robot.isPresent()) {
				FleetLogRobots newRobot = FleetLogRobots.builder().name(robotName).build();
				newRobot.setFleetLogWithoutRelation(fleetLog);
				newRobots.add(newRobot);
			}
		}
		fleetLogRobotRepository.saveAll(newRobots);
		saveFleetRobotsLogs(fleetLog, fleetLogApi.getRobots());
	}

	/**
	 * FleetLogRobots의 로그 데이터를 저장한다.
	 */
	@Transactional
	public void saveFleetRobotsLogs(FleetLog fleetLog, Map<String, List<LogEntryApi>> robots) {
		for (Map.Entry<String, List<LogEntryApi>> entry : robots.entrySet()) {
			String robotName = entry.getKey();
			List<LogEntryApi> robotLogs = entry.getValue();
			FleetLogRobots robot = fleetLogRobotRepository.findByFleetAndName(fleetLog, robotName).get();
			List<LogEntryApi> newRobotLogs = getNewRobotLogs(robotLogs, robot);
			saveFleetLogRobotLogs(robot, newRobotLogs);
		}
	}

	/**
	 * FleetLogRobots의 로그 중 새로운 로그를 필터링한다.
	 */
	@Transactional
	public List<LogEntryApi> getNewRobotLogs(List<LogEntryApi> logs, FleetLogRobots robot) {
		List<Integer> seqList = logs.stream().map(LogEntryApi::getSeq).collect(Collectors.toList());
		List<Integer> existingSeq = fleetLogRobotLogRepository.findSeqByRobotIdAndSeqIn(robot.getId(), seqList);
		return logs
			.stream()
			.filter(log -> !existingSeq.contains(log.getSeq()))
			.collect(Collectors.toList());
	}

	/**
	 * FleetLogRobotsLog 데이터를 벌크 저장한다.
	 */
	@Transactional
	public void saveFleetLogRobotLogs(FleetLogRobots robot, List<LogEntryApi> robotLogs) {
		List<FleetLogRobotsLog> fleetLogLogs = robotLogs.stream()
			.map(LogEntryApi.MapStruct.INSTANCE::toFleetLogRobotsLogEntity)
			.map((log) -> log.setFleetLogRobotWithoutRelation(robot))
			.collect(Collectors.toList());
		fleetLogRobotLogRepository.saveAll(fleetLogLogs);
	}
}
