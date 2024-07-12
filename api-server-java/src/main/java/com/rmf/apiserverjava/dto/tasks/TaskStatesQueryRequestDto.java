package com.rmf.apiserverjava.dto.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskStatesQueryRequestDto {

	@JsonProperty("task_id")
	private String taskId;
	private String category;
	@JsonProperty("assigned_to")
	private String assignedTo;
	@JsonProperty("start_time_between")
	private String startTimeBetween;
	@JsonProperty("finish_time_between")
	private String finishTimeBetween;
	private String status;
	private Integer limit;
	private Integer offset;
	@JsonProperty("order_by")
	private String orderBy;

	@Builder
	public TaskStatesQueryRequestDto(String taskId, String category, String assignedTo,
		String startTimeBetween, String finishTimeBetween, String status,
		Integer limit, Integer offset, String orderBy) {
		this.taskId = taskId;
		this.category = category;
		this.assignedTo = assignedTo;
		this.startTimeBetween = startTimeBetween;
		this.finishTimeBetween = finishTimeBetween;
		this.status = status;
		this.limit = limit;
		this.offset = offset;
		this.orderBy = orderBy;
	}

}
