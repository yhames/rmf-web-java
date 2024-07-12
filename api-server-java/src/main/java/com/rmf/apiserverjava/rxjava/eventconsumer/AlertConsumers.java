package com.rmf.apiserverjava.rxjava.eventconsumer;

import org.springframework.stereotype.Component;

import com.rmf.apiserverjava.dto.alerts.AlertResponseDto;
import com.rmf.apiserverjava.rxjava.eventbus.AlertEvents;

import io.reactivex.rxjava3.core.Observable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * EventConsumer.
 *
 * <p>
 *	Alert 이벤트를 구독하는 이벤트 컨슈머. shared()를 통해 다수의 구독자가 동일한 이벤트를 구독할 수 있다.
 * </p>
 */
@Component
@Getter
@Slf4j
public class AlertConsumers {
	private final AlertEvents alertEvents;

	private final Observable<AlertResponseDto> alertsConsumer;

	public AlertConsumers(AlertEvents alertEvents) {
		this.alertEvents = alertEvents;
		alertsConsumer = alertEvents.getAlertsEvent().share();
		alertsConsumer.doOnNext(data -> log.info("Alert Data received: " + data));
	}
}
