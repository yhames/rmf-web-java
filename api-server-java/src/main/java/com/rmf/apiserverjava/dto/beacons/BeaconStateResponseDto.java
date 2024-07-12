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

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BeaconStateResponseDto {

	@NotNull
	@NotEmpty
	private String id;

	@NotNull
	private Boolean online;

	private String category;

	@NotNull
	private Boolean activated;

	private String level;

	@Builder
	public BeaconStateResponseDto(String id, Boolean online, String category, Boolean activated, String level) {
		this.id = id;
		this.online = online;
		this.category = category;
		this.activated = activated;
		this.level = level;
	}

	@Mapper
	public interface MapStruct {
		BeaconStateResponseDto.MapStruct INSTANCE = Mappers.getMapper(BeaconStateResponseDto.MapStruct.class);

		@Mapping(source = "online", target = "online", qualifiedByName = "intToBoolean")
		@Mapping(source = "activated", target = "activated", qualifiedByName = "intToBoolean")
		BeaconStateResponseDto toDto(BeaconState beaconState);

		/**
		 * int를 boolean으로 변환한다.
		 */
		@Named("intToBoolean")
		default boolean mapIntToBoolean(int value) {
			return value == 1;
		}
	}
}
