package com.rmf.apiserverjava.entity.health;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HealthStatus {
	Healthy("Healthy", 0),
	Unhealthy("Unhealthy", 1),
	Dead("Dead", 2);

	private final String value;

	private final int criticality;

	public boolean isEqual(HealthStatus healthStatus) {
		return this.criticality == healthStatus.criticality;
	}
}
