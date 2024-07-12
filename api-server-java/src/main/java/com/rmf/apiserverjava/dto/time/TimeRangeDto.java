package com.rmf.apiserverjava.dto.time;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeRangeDto {
	long startTimeMillis;
	long endTimeMillis;

	public TimeRangeDto(long startTimeMillis, long endTimeMillis) {
		this.startTimeMillis = startTimeMillis;
		this.endTimeMillis = endTimeMillis;
	}
}
