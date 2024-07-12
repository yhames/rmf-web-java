package com.rmf.apiserverjava.dto.alerts;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CreateAlertRequestDto.
 *
 * <p>
 *     Alert 생성 요청 쿼리 파라미터를 받기 위한 DTO.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateAlertRequestDto {
	@NotEmpty
	@NotNull
	private String alert_id;

	@NotEmpty
	@NotNull
	private String category;
}
