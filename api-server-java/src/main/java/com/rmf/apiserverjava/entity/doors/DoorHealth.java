package com.rmf.apiserverjava.entity.doors;

import com.rmf.apiserverjava.entity.health.Health;
import com.rmf.apiserverjava.entity.health.HealthStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doorhealth")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DoorHealth extends Health {

	@Builder
	public DoorHealth(String id, HealthStatus healthStatus, String healthMessage) {
		super(id, healthStatus, healthMessage);
	}
}
