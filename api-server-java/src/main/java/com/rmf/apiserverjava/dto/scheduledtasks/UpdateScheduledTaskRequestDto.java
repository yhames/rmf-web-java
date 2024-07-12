package com.rmf.apiserverjava.dto.scheduledtasks;

import java.util.List;

import com.rmf.apiserverjava.rmfapi.tasks.TaskRequestApi;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateScheduledTaskRequestDto {

	@NotNull
	private TaskRequestApi task_request;

	@NotNull
	private List<ScheduledTaskScheduleRequestDto> schedules;

	@Builder
	public UpdateScheduledTaskRequestDto(List<ScheduledTaskScheduleRequestDto> schedules, TaskRequestApi task_request) {
		this.schedules = schedules;
		this.task_request = task_request;
	}
}
