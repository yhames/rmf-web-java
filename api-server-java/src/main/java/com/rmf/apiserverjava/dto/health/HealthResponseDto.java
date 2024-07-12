package com.rmf.apiserverjava.dto.health;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rmf.apiserverjava.entity.health.Health;
import com.rmf.apiserverjava.entity.health.HealthStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HealthResponseDto {

	@JsonProperty("id")
	private String id;

	@JsonProperty("health_status")
	private HealthStatus healthStatus;

	@JsonProperty("health_message")
	private String healthMessage;

	@Builder
	public HealthResponseDto(String id, HealthStatus healthStatus, String healthMessage) {
		this.id = id;
		this.healthStatus = healthStatus;
		this.healthMessage = healthMessage;
	}

	@Mapper
	public interface MapStruct {

		MapStruct INSTANCE = Mappers.getMapper(MapStruct.class);

		HealthResponseDto toDto(Health health);
	}
}
