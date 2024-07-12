package com.rmf.apiserverjava.service;

import java.util.List;
import java.util.Optional;

import com.rmf.apiserverjava.dto.beacons.CreateBeaconStateRequestDto;
import com.rmf.apiserverjava.entity.beacons.BeaconState;

/**
 * Service for beacons
 * */
public interface BeaconsService {

	// get all beacon states
	public List<BeaconState> getAll();

	// get all states by beacon_id
	public Optional<BeaconState> getOrNone(String beaconId);

	// if beacon state exists update it, otherwise create it
	public BeaconState updateOrCreate(CreateBeaconStateRequestDto beaconStateDto);
}
