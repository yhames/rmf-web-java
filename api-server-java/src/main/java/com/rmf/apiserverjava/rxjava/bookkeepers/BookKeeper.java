package com.rmf.apiserverjava.rxjava.bookkeepers;

import org.springframework.stereotype.Component;

import com.rmf.apiserverjava.entity.buildingmaps.BuildingMap;
import com.rmf.apiserverjava.entity.doors.DoorHealth;
import com.rmf.apiserverjava.entity.doors.DoorState;
import com.rmf.apiserverjava.rxjava.eventbus.RmfEvents;
import com.rmf.apiserverjava.service.BuildingMapService;
import com.rmf.apiserverjava.service.DoorService;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookKeeper {

	private final BuildingMapService buildingMapService;

	private final DoorService doorService;

	private final RmfEvents rmfEvents;

	private final CompositeDisposable disposable = new CompositeDisposable();

	@PostConstruct
	private void onStart() {
		disposable.add(rmfEvents.getBuildingMapEvent().subscribe(this::recordBuildingMap));
		disposable.add(rmfEvents.getDoorStateEvent().subscribe(this::recordDoorState));
		disposable.add(rmfEvents.getDoorHealthEvent().subscribe(this::recordDoorHealth));
	}

	@PreDestroy
	private void onExit() {
		disposable.dispose();
	}

	private void recordBuildingMap(BuildingMap buildingMap) {
		log.info("Building Map replaced: {}", buildingMap.getId());
		buildingMapService.updateOrCreate(buildingMap);
	}

	private void recordDoorHealth(DoorHealth doorHealth) {
		log.info("DoorHealth replaced: {}", doorHealth.getId());
		doorService.updateOrCreateDoorHealth(doorHealth);
	}

	private void recordDoorState(DoorState doorState) {
		log.info("DoorState replaced: {}", doorState.getId());
		doorService.updateOrCreateDoorState(doorState);
	}
}
