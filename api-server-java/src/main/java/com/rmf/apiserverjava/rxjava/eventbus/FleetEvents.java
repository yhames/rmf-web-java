package com.rmf.apiserverjava.rxjava.eventbus;

import org.springframework.stereotype.Component;

import com.rmf.apiserverjava.rmfapi.fleet.FleetLogApi;
import com.rmf.apiserverjava.rmfapi.fleet.FleetStateApi;

import io.reactivex.rxjava3.subjects.PublishSubject;
import lombok.Getter;

/**
 * FleetEvents.
 *
 * <p>
 *   Fleet 이벤트를 발행하는 이벤트 버스
 * </p>
 */
@Component
@Getter
public class FleetEvents {
	private final PublishSubject<FleetStateApi> fleetStatesEvent;
	private final PublishSubject<FleetLogApi> fleetEventLogsEvent;

	public FleetEvents() {
		fleetStatesEvent = PublishSubject.create();
		fleetEventLogsEvent = PublishSubject.create();
	}
}
