package com.rmf.apiserverjava.rxjava.eventbus;

import org.springframework.stereotype.Component;

import com.rmf.apiserverjava.dto.beacons.BeaconStateResponseDto;

import io.reactivex.rxjava3.subjects.PublishSubject;
import lombok.Getter;

/**
 * BeaconEvents.
 *
 * <p>
 * 	Beacon 이벤트를 발행하는 이벤트 버스
 * </p>
 */
@Component
@Getter
public class BeaconEvents {
	private final PublishSubject<BeaconStateResponseDto> beaconsEvent;

	public BeaconEvents() {
		beaconsEvent = PublishSubject.create();
	}
}
