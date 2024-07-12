package com.rmf.apiserverjava.dto.alerts;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rmf.apiserverjava.entity.alerts.Alert;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AlertResponseDto.
 *
 * <p>
 *	Alert Entity의 전체 정보를 반환하기 위한 DTO. json 응답시 cammel case를 snake case로 변환한다.
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AlertResponseDto {
	private String id;
	@JsonProperty("original_id")
	private String originalId;
	private Alert.Category category;
	@JsonProperty("unix_millis_created_time")
	private long unixMillisCreatedTime;
	@JsonProperty("acknowledged_by")
	private String acknowledgedBy;
	@JsonProperty("unix_millis_acknowledged_time")
	private Long unixMillisAcknowledgedTime;

	public AlertResponseDto(String id, String originalId, Alert.Category category, long unixMillisCreatedTime,
		String acknowledgedBy, Long unixMillisAcknowledgedTime) {
		this.id = id;
		this.originalId = originalId;
		this.category = category;
		this.unixMillisCreatedTime = unixMillisCreatedTime;
		this.acknowledgedBy = acknowledgedBy;
		this.unixMillisAcknowledgedTime = unixMillisAcknowledgedTime;
	}

	@Mapper
	public interface MapStruct {
		MapStruct INSTANCE = Mappers.getMapper(MapStruct.class);

		AlertResponseDto toDto(Alert alert);
	}
}
