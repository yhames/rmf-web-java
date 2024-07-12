package com.rmf.apiserverjava.entity.health;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Health {

	@Id
	@Column(name = "id", nullable = false, unique = true, columnDefinition = "VARCHAR(255)")
	private String id;

	@Enumerated(EnumType.STRING)
	@Column(name = "health_status", columnDefinition = "VARCHAR(255)")
	private HealthStatus healthStatus;

	@Column(name = "health_message", columnDefinition = "TEXT")
	private String healthMessage;

	protected Health(String id, HealthStatus healthStatus, String healthMessage) {
		this.id = id;
		this.healthStatus = healthStatus;
		this.healthMessage = healthMessage;
	}

	public void updateHealthStatus(Health health) {
		if (this.healthStatus.isEqual(health.getHealthStatus())) {
			return;
		}
		this.healthStatus = health.getHealthStatus();
		this.healthMessage = health.getHealthMessage();
	}
}
