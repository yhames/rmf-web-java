package com.rmf.apiserverjava.rxjava.eventbus;

import org.springframework.stereotype.Component;

import com.rmf.apiserverjava.dto.alerts.AlertResponseDto;

import io.reactivex.rxjava3.subjects.PublishSubject;
import lombok.Getter;

/**
 * AlertEvents.
 *
 * <p>
 *	Alert 이벤트를 발행하는 이벤트 버스
 * </p>
 */
@Component
@Getter
public class AlertEvents {
	private final PublishSubject<AlertResponseDto> alertsEvent;

	public AlertEvents() {
		alertsEvent = PublishSubject.create();
	}
}
