package com.rmf.apiserverjava.rxjava.eventbus;

import org.springframework.stereotype.Component;

import com.rmf.apiserverjava.entity.buildingmaps.BuildingMap;
import com.rmf.apiserverjava.entity.doors.DoorHealth;
import com.rmf.apiserverjava.entity.doors.DoorState;
import com.rmf.apiserverjava.service.BuildingMapService;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * RmfEvents.
 *
 * <p>
 *  RMF Core와 연관된 이벤트를 발행하는 이벤트 버스
 * </p>
 */
@Component
@Getter
public class RmfEvents {
	private final PublishSubject<DoorState> doorStateEvent;
	private final PublishSubject<DoorHealth> doorHealthEvent;
	private final PublishSubject<String> liftStatesEvent;
	private final PublishSubject<String> liftHealthEvent;
	private final PublishSubject<String> dispenserStatesEvent;
	private final PublishSubject<String> dispenserHealthEvent;
	private final PublishSubject<String> ingestorStatesEvent;
	private final PublishSubject<String> ingestorHealthEvent;
	private final PublishSubject<String> fleetStatesEvent;
	private final PublishSubject<String> robotHealthEvent;
	private final @NotNull BehaviorSubject<BuildingMap> buildingMapEvent;
	private final BuildingMapService buildingMapService;

	public RmfEvents(BuildingMapService buildingMapService) {
		this.buildingMapService = buildingMapService;

		doorStateEvent = PublishSubject.create();
		doorHealthEvent = PublishSubject.create();
		liftStatesEvent = PublishSubject.create();
		liftHealthEvent = PublishSubject.create();
		dispenserStatesEvent = PublishSubject.create();
		dispenserHealthEvent = PublishSubject.create();
		ingestorStatesEvent = PublishSubject.create();
		ingestorHealthEvent = PublishSubject.create();
		fleetStatesEvent = PublishSubject.create();
		robotHealthEvent = PublishSubject.create();
		buildingMapEvent = BehaviorSubject.create();
	}

	/**
	 * onStart.
	 *
	 * <p>
	 *     BuildingMapService를 통해 건물 맵을 조회하여 발행한다.
	 *     DB에 저장된 건물 맵이 없을 경우 아무런 이벤트를 발행하지 않는다.
	 *     이때 buildingMapEvent.getValue()의 값은 null이다.
	 * </p>
	 */
	@PostConstruct
	private void onStart() {
		buildingMapService.getBuildingMap().ifPresent(buildingMapEvent::onNext);
	}
}
