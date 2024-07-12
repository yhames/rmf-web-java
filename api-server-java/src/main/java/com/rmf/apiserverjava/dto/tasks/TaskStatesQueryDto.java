package com.rmf.apiserverjava.dto.tasks;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rmf.apiserverjava.dto.time.TimeRangeDto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskStatesQueryDto {

	@JsonProperty("id")
	private List<String> id;
	private List<String> category;
	@JsonProperty("assigned_to")
	private List<String> assignedTo;
	@JsonProperty("start_time_between")
	private TimeRangeDto startTimeBetween;
	@JsonProperty("finish_time_between")
	private TimeRangeDto finishTimeBetween;
	private List<String> status;

	@Builder
	public TaskStatesQueryDto(List<String> id, List<String> category, List<String> assignedTo,
		TimeRangeDto startTimeBetween, TimeRangeDto finishTimeBetween, List<String> status) {
		this.id = id;
		this.category = category;
		this.assignedTo = assignedTo;
		this.startTimeBetween = startTimeBetween;
		this.finishTimeBetween = finishTimeBetween;
		this.status = status;
	}
}
