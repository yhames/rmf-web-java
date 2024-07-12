package com.rmf.apiserverjava.dto.tasks;

import com.rmf.apiserverjava.dto.time.TimeRangeDto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskLogRequestDto {

	String taskId;
	TimeRangeDto timeRange;

	@Builder
	public TaskLogRequestDto(String taskId, TimeRangeDto timeRange) {
		this.taskId = taskId;
		this.timeRange = timeRange;
	}
}
