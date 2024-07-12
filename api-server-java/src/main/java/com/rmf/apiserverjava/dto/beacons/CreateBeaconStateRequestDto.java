package com.rmf.apiserverjava.dto.beacons;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.rmf.apiserverjava.entity.beacons.BeaconState;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for beacon state
 * */
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateBeaconStateRequestDto {

	@NotNull
	@NotEmpty
	private String beacon_id;

	@NotNull
	private Boolean online;

	private String category;

	@NotNull
	private Boolean activated;

	private String level;

	@Builder
	public CreateBeaconStateRequestDto(String beacon_id, Boolean online,
		String category, Boolean activated, String level) {
		this.beacon_id = beacon_id;
		this.online = online;
		this.category = category;
		this.activated = activated;
		this.level = level;
	}
}
