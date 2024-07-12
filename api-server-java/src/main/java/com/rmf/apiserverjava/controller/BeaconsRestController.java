package com.rmf.apiserverjava.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.rmf.apiserverjava.dto.beacons.BeaconStateResponseDto;
import com.rmf.apiserverjava.dto.beacons.CreateBeaconStateRequestDto;

/**
 * Rest controller for beacons
 * */
public interface BeaconsRestController {

	// get all beacon states endpoint(get : /beacons)
	public List<BeaconStateResponseDto> getBeacons();

	// get all states by beacon_id endpoint(get : /beacons/{beaconId})
	public ResponseEntity<BeaconStateResponseDto> getBeacon(String beaconId);

	// save beacon state endpoint(post : /beacons)
	public ResponseEntity<BeaconStateResponseDto> saveBeaconState(CreateBeaconStateRequestDto beaconStateDto);
}
