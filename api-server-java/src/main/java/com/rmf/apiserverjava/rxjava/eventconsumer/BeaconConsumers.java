package com.rmf.apiserverjava.rxjava.eventconsumer;

import org.springframework.stereotype.Component;

import com.rmf.apiserverjava.dto.beacons.BeaconStateResponseDto;
import com.rmf.apiserverjava.rxjava.eventbus.BeaconEvents;

import io.reactivex.rxjava3.core.Observable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * EventConsumer.
 *
 * <p>
 *   Beacon 이벤트를 구독하는 이벤트 컨슈머. shared()를 통해 다수의 구독자가 동일한 이벤트를 구독할 수 있다.
 * </p>
 */
@Component
@Getter
@Slf4j
public class BeaconConsumers {
	private final BeaconEvents beaconEvents;

	private final Observable<BeaconStateResponseDto> beaconsConsumer;

	public BeaconConsumers(BeaconEvents beaconEvents) {
		this.beaconEvents = beaconEvents;
		beaconsConsumer = beaconEvents.getBeaconsEvent().share();
		beaconsConsumer.doOnNext(data -> log.info("Beacon Data received: " + data));
	}
}
