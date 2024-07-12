package com.rmf.apiserverjava.rxjava.eventconsumer;

import org.springframework.stereotype.Component;

import com.rmf.apiserverjava.rmfapi.fleet.FleetLogApi;
import com.rmf.apiserverjava.rmfapi.fleet.FleetStateApi;
import com.rmf.apiserverjava.rxjava.eventbus.FleetEvents;

import io.reactivex.rxjava3.core.Observable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * FleetConsumers.
 *
 * <p>
 *     Fleet 이벤트를 구독하는 이벤트 컨슈머. shared()를 통해 다수의 구독자가 동일한 이벤트를 구독할 수 있다.
 * </p>
 */
@Component
@Getter
@Slf4j
public class FleetConsumers {
	private final FleetEvents fleetEvents;

	private final Observable<FleetStateApi> fleetStatesConsumer;
	private final Observable<FleetLogApi> fleetLogsConsumer;

	public FleetConsumers(FleetEvents fleetEvents) {
		this.fleetEvents = fleetEvents;
		fleetStatesConsumer = fleetEvents.getFleetStatesEvent().share();
		fleetStatesConsumer.doOnNext(data -> log.info("Fleet State Data received: " + data));

		fleetLogsConsumer = fleetEvents.getFleetEventLogsEvent().share();
		fleetLogsConsumer.doOnNext(data -> log.info("Fleet Log Data received: " + data));
	}
}
