package com.rmf.apiserverjava.dto.alerts;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.rmf.apiserverjava.entity.alerts.Alert;
import com.rmf.apiserverjava.global.exception.custom.BusinessException;
import com.rmf.apiserverjava.global.exception.custom.InvalidClientArgumentException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CreateAlertDto.
 *
 * <p>
 *     Alert 생성을 위한 DTO.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateAlertDto {
	public static final String VALUE_NOT_EXIST_ALERT_CATEGORY = "VALUE NOT EXISTS IN ALERT CATEGORY";
	private String alertId;

	private Alert.Category category;

	public CreateAlertDto(String alertId, Alert.Category category) {
		this.alertId = alertId;
		this.category = category;
	}

	@Mapper
	public interface MapStruct {
		CreateAlertDto.MapStruct INSTANCE = Mappers.getMapper(CreateAlertDto.MapStruct.class);

		/**
		 * CreateAlertRequestDto를 CreateAlertDto로 변환한다.
		 */
		@Mapping(source = "alert_id", target = "alertId")
		@Mapping(source = "category", target = "category", qualifiedByName = "valueToCategory")
		CreateAlertDto toDto(CreateAlertRequestDto requestDto);

		/**
		 * CreateAlertDto를 Alert 엔티티로 변환한다.
		 */
		@Mapping(source = "alertId", target = "id")
		@Mapping(source = "alertId", target = "originalId")
		@Mapping(target = "unixMillisCreatedTime", expression = "java(System.currentTimeMillis())")
		Alert toEntity(CreateAlertDto createAlertDto);

		/**
		 * value를 Alert.Category Enum으로 변환한다.
		 */
		@Named("valueToCategory")
		default Alert.Category mapValueToCategory(String value) {
			return Alert.Category.fromValue(value)
				.orElseThrow(() -> new InvalidClientArgumentException(VALUE_NOT_EXIST_ALERT_CATEGORY));
		}
	}
}
