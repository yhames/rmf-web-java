package com.rmf.apiserverjava.rxjava.watchdog;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.rmf.apiserverjava.entity.buildingmaps.BuildingMap;
import com.rmf.apiserverjava.entity.doors.DoorHealth;
import com.rmf.apiserverjava.entity.doors.DoorState;
import com.rmf.apiserverjava.entity.health.HealthStatus;
import com.rmf.apiserverjava.rosmsgs.door.DoorModeMsg;
import com.rmf.apiserverjava.rxjava.eventbus.RmfEvents;
import com.rmf.apiserverjava.rxjava.watchdog.operators.HealthOperators;
import com.rmf.apiserverjava.service.BuildingMapService;
import com.rmf.apiserverjava.service.DoorService;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Timed;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DoorHealthWatchDog {

	private final Map<String, BehaviorSubject<DoorState>> subjects = new ConcurrentHashMap<>();

	private final PublishSubject<DoorHealth> doorHealthEvent;

	private final BuildingMapService buildingMapService;

	public DoorHealthWatchDog(RmfEvents rmfEvents, BuildingMapService buildingMapService) {
		this.doorHealthEvent = rmfEvents.getDoorHealthEvent();
		this.buildingMapService = buildingMapService;

		rmfEvents.getDoorStateEvent().subscribe(this::onState);
		log.info("DoorHealth WatchDog is started");
	}

	@PostConstruct
	private void onStart() {
		Optional<BuildingMap> result = buildingMapService.getBuildingMap();
		if (result.isEmpty()) {
			return;
		}
		result.get().getData().getLevels().forEach(level -> level.getDoors()
			.forEach(door -> subjects.put(door.getName(), BehaviorSubject.create())));
		subjects.forEach(this::watch);
	}

	private void onState(DoorState doorState) {
		String doorName = doorState.getId();
		if (subjects.containsKey(doorName)) {
			subjects.get(doorName).onNext(doorState);
		} else {
			BehaviorSubject<DoorState> subject = BehaviorSubject.create();
			subjects.put(doorName, subject);
			watch(doorName, subject);
		}
	}

	private void watch(String doorName, Observable<DoorState> observable) {
		Observable<Timed<DoorHealth>> doorHealthObservable = observable
			.map(DoorModeMsg::toDoorHealth)
			.distinctUntilChanged()
			.timestamp();
		observable.compose(HealthOperators.getHeartBeat())
			.map((hasHeartBeat) -> heartBeatToDoorHealth(doorName, hasHeartBeat))
			.timestamp()
			.compose(HealthOperators.combineMostCritical(doorHealthObservable))
			.subscribe(doorHealthEvent::onNext);
	}

	private DoorHealth heartBeatToDoorHealth(String doorName, boolean hasHeartBeat) {
		if (hasHeartBeat) {
			return DoorHealth.builder()
				.id(doorName)
				.healthStatus(HealthStatus.Healthy)
				.healthMessage("")
				.build();
		}
		return DoorHealth.builder()
			.id(doorName)
			.healthStatus(HealthStatus.Dead)
			.healthMessage(HealthOperators.HEART_BEAT_FAILED)
			.build();
	}
}
