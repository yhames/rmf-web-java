package com.rmf.apiserverjava.rxjava.watchdog.operators;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.rmf.apiserverjava.entity.health.Health;
import com.rmf.apiserverjava.global.exception.custom.BusinessException;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.schedulers.Timed;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HealthOperators {

	public static final String HEART_BEAT_FAILED = "heartbeat failed";

	private static final String RMF_OPERATORS_FAILED = "RMF Operator failed: No health status found.";

	private static final int LIVELINESS = 10;

	public static <T> ObservableTransformer<T, Boolean> getHeartBeat() {
		return upstream -> upstream.buffer(LIVELINESS, TimeUnit.SECONDS)
			.map(buffer -> !buffer.isEmpty())
			.distinctUntilChanged();
	}

	public static <T extends Health> ObservableTransformer<Timed<T>, T> combineMostCritical(
		Observable<Timed<T>> health) {
		return upstream -> Observable.combineLatest(upstream, health, List::of)
			.map(HealthOperators::getMostCritical);
	}

	public static <T extends Health> T getMostCritical(List<Timed<T>> healths) {
		return healths.stream()
			.max((health1, health2) -> {
				int critical1 = health1.value().getHealthStatus().getCriticality();
				int critical2 = health2.value().getHealthStatus().getCriticality();
				if (critical1 != critical2) {
					return Integer.compare(critical1, critical2);
				}
				return Long.compare(health2.time(), health1.time());
			}).orElseThrow(() -> new BusinessException(RMF_OPERATORS_FAILED)).value();
	}
}
