package com.rmf.apiserverjava.dto.fleets;

import com.rmf.apiserverjava.dto.time.TimeRangeDto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetFleetLogsDto {
	String fleetStateName;
	TimeRangeDto timeRange;

	@Builder
	public GetFleetLogsDto(String fleetStateName, TimeRangeDto timeRange) {
		this.fleetStateName = fleetStateName;
		this.timeRange = timeRange;
	}
}
