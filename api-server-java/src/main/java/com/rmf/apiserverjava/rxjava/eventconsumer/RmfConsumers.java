package com.rmf.apiserverjava.rxjava.eventconsumer;

import org.springframework.stereotype.Component;

import com.rmf.apiserverjava.entity.buildingmaps.BuildingMap;
import com.rmf.apiserverjava.entity.doors.DoorHealth;
import com.rmf.apiserverjava.entity.doors.DoorState;
import com.rmf.apiserverjava.rxjava.eventbus.RmfEvents;

import io.reactivex.rxjava3.core.Observable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * RmfConsumers.
 *
 * <p>
 *  RMF 이벤트를 구독하는 이벤트 컨슈머. shared()를 통해 다수의 구독자가 동일한 이벤트를 구독할 수 있다.
 * </p>
 */
@Component
@Getter
@Slf4j
public class RmfConsumers {
	private final RmfEvents RmfEvents;

	private final Observable<DoorState> doorStateConsumer;
	private final Observable<DoorHealth> doorHealthConsumer;
	private final Observable<String> liftStatesConsumer;
	private final Observable<String> liftHealthConsumer;
	private final Observable<String> dispenserStatesConsumer;
	private final Observable<String> dispenserHealthConsumer;
	private final Observable<String> ingestorStatesConsumer;
	private final Observable<String> ingestorHealthConsumer;
	private final Observable<String> fleetStatesConsumer;
	private final Observable<String> robotHealthConsumer;
	private final Observable<BuildingMap> buildingMapConsumer;

	public RmfConsumers(RmfEvents RmfEvents) {
		this.RmfEvents = RmfEvents;
		doorStateConsumer = RmfEvents.getDoorStateEvent().share();
		doorStateConsumer.doOnNext(data -> log.info("DoorStateConsumer Data received: " + data));

		doorHealthConsumer = RmfEvents.getDoorHealthEvent().share();
		doorHealthConsumer.doOnNext(data -> log.info("DoorHealthConsumer Data received: " + data));

		liftStatesConsumer = RmfEvents.getLiftStatesEvent().share();
		liftStatesConsumer.doOnNext(data -> log.info("LiftStatesConsumer Data received: " + data));

		liftHealthConsumer = RmfEvents.getLiftHealthEvent().share();
		liftHealthConsumer.doOnNext(data -> log.info("LiftHealthConsumer Data received: " + data));

		dispenserStatesConsumer = RmfEvents.getDispenserStatesEvent().share();
		dispenserStatesConsumer.doOnNext(data -> log.info("DispenserStatesConsumer Data received: " + data));

		dispenserHealthConsumer = RmfEvents.getDispenserHealthEvent().share();
		dispenserHealthConsumer.doOnNext(data -> log.info("DispenserHealthConsumer Data received: " + data));

		ingestorStatesConsumer = RmfEvents.getIngestorStatesEvent().share();
		ingestorStatesConsumer.doOnNext(data -> log.info("IngestorStatesConsumer Data received: " + data));

		ingestorHealthConsumer = RmfEvents.getIngestorHealthEvent().share();
		ingestorHealthConsumer.doOnNext(data -> log.info("IngestorHealthConsumer Data received: " + data));

		fleetStatesConsumer = RmfEvents.getFleetStatesEvent().share();
		fleetStatesConsumer.doOnNext(data -> log.info("FleetStatesConsumer Data received: " + data));

		robotHealthConsumer = RmfEvents.getRobotHealthEvent().share();
		robotHealthConsumer.doOnNext(data -> log.info("RobotHealthConsumer Data received: " + data));

		buildingMapConsumer = RmfEvents.getBuildingMapEvent().share();
		buildingMapConsumer.doOnNext(data -> log.info("BuildingMapConsumer Data received: " + data));
	}
}
