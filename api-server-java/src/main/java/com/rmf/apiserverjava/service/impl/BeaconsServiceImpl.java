package com.rmf.apiserverjava.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rmf.apiserverjava.dto.beacons.BeaconStateResponseDto;
import com.rmf.apiserverjava.dto.beacons.CreateBeaconStateRequestDto;
import com.rmf.apiserverjava.entity.beacons.BeaconState;
import com.rmf.apiserverjava.global.exception.custom.NotFoundException;
import com.rmf.apiserverjava.repository.BeaconsRepository;
import com.rmf.apiserverjava.rxjava.eventbus.BeaconEvents;
import com.rmf.apiserverjava.service.BeaconsService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of the BeaconsService
 * */
@Service
@RequiredArgsConstructor
public class BeaconsServiceImpl implements BeaconsService {

	private final BeaconsRepository beaconsRepository;
	private final BeaconEvents beaconEvents;

	private static final String BEACON_SAVE_FAILED = "Could not save beacon state with %s";


	// get all beacon states by calling the repository
	@Override
	public List<BeaconState> getAll() {
		List<BeaconState> all = beaconsRepository.findAll();
		return all;
	}

	// get all states by beacon_id by calling the repository
	@Override
	public Optional<BeaconState> getOrNone(String beaconId) {
		return beaconsRepository.findById(beaconId);
	}

	// find beacon state by id and update it, otherwise save new one
	@Override
	@Transactional
	public BeaconState updateOrCreate(CreateBeaconStateRequestDto beaconStateDto) {
		BeaconState beaconState;

		Optional<BeaconState> beaconStateOptional = beaconsRepository.findById(beaconStateDto.getBeacon_id());
		if (beaconStateOptional.isPresent()) {
			BeaconState existing = beaconStateOptional.get();
			existing.updateBeaconState(
				beaconStateDto.getOnline() ? 1 : 0,
				beaconStateDto.getCategory(),
				beaconStateDto.getActivated() ? 1 : 0,
				beaconStateDto.getLevel());
			beaconState = existing;
		} else {
			BeaconState newBeaconState = BeaconState.builder()
				.id(beaconStateDto.getBeacon_id())
				.online(beaconStateDto.getOnline() ? 1 : 0)
				.category(beaconStateDto.getCategory())
				.activated(beaconStateDto.getActivated() ? 1 : 0)
				.level(beaconStateDto.getLevel())
				.build();
			BeaconState saved = beaconsRepository.save(newBeaconState);
			beaconState = saved;
		}
		if (beaconState == null) {
			throw new NotFoundException(String.format(BEACON_SAVE_FAILED, beaconStateDto.getBeacon_id()));
		}
		beaconEvents.getBeaconsEvent().onNext(BeaconStateResponseDto.MapStruct.INSTANCE.toDto(beaconState));
		return beaconState;
	}
}
