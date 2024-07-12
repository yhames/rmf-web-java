package com.rmf.apiserverjava.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rmf.apiserverjava.entity.doors.DoorHealth;
import com.rmf.apiserverjava.entity.doors.DoorState;
import com.rmf.apiserverjava.global.exception.custom.BusinessException;
import com.rmf.apiserverjava.repository.DoorHealthRepository;
import com.rmf.apiserverjava.repository.DoorStateRepository;
import com.rmf.apiserverjava.service.DoorService;

import lombok.RequiredArgsConstructor;

/**
 * DoorServiceImpl
 *
 * <p>
 *     DoorService의 구현체
 * </p>
 */
@Service
@RequiredArgsConstructor
public class DoorServiceImpl implements DoorService {

	private static final String DOOR_HEALTH_CREATE_FAILED = "DoorHealth 업데이트에 실패했습니다: ";

	private static final String DOOR_STATE_CREATE_FAILED = "DoorState 업데이트에 실패했습니다: ";

	private final DoorStateRepository doorStateRepository;

	private final DoorHealthRepository doorHealthRepository;

	/**
	 * repository에 DoorState 전체 조회를 요청한다.
	 */
	@Override
	public List<DoorState> getDoorStates() {
		return doorStateRepository.findAll();
	}

	/**
	 * repository에 DoorState 단일 조회를 요청한다.
	 */
	@Override
	public Optional<DoorState> getDoorState(String doorName) {
		return doorStateRepository.findById(doorName);
	}

	/**
	 * repository에 DoorHealth 단일 조회를 요청한다.
	 */
	@Override
	public Optional<DoorHealth> getDoorHealth(String doorName) {
		return doorHealthRepository.findById(doorName);
	}

	/**
	 * DoorHealth를 업데이트하거나 생성한다.
	 */
	@Override
	@Transactional
	public Optional<DoorHealth> updateOrCreateDoorHealth(DoorHealth doorHealth) {
		try {
			doorHealthRepository.findById(doorHealth.getId()).ifPresentOrElse(
				(findDoorHealth) -> findDoorHealth.updateHealthStatus(doorHealth),
				() -> doorHealthRepository.save(doorHealth)
			);
			return Optional.of(doorHealth);
		} catch (Exception e) {
			throw new BusinessException(DOOR_HEALTH_CREATE_FAILED + doorHealth.getId());
		}
	}

	@Override
	@Transactional
	public Optional<DoorState> updateOrCreateDoorState(DoorState doorState) {
		try {
			doorStateRepository.findById(doorState.getId()).ifPresentOrElse(
				(findDoorState) -> findDoorState.updateDoorStateMsg(doorState),
				() -> doorStateRepository.save(doorState)
			);
			return Optional.of(doorState);
		} catch (Exception e) {
			throw new BusinessException(DOOR_STATE_CREATE_FAILED + doorState.getId());
		}
	}
}
