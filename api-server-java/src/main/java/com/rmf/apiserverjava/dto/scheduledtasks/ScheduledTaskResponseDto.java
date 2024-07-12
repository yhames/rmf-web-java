package com.rmf.apiserverjava.dto.scheduledtasks;

import java.sql.Timestamp;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTask;
import com.rmf.apiserverjava.rmfapi.tasks.TaskRequestApi;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduledTaskResponseDto {

	private int id;

	@JsonProperty("task_request")
	private TaskRequestApi taskRequest;

	@JsonProperty("created_by")
	private String createdBy;

	@JsonProperty("last_ran")
	private Timestamp lastRan;

	@JsonProperty("except_dates")
	private List<String> exceptDates;

	private List<ScheduledTaskScheduleResponseDto> schedules;

	@Builder
	public ScheduledTaskResponseDto(int id, TaskRequestApi taskRequest, String createdBy, Timestamp lastRan,
		List<String> exceptDates, List<ScheduledTaskScheduleResponseDto> schedules) {
		this.id = id;
		this.taskRequest = taskRequest;
		this.createdBy = createdBy;
		this.lastRan = lastRan;
		this.exceptDates = exceptDates;
		this.schedules = schedules;
	}

	@Mapper
	public interface MapStruct {

		ScheduledTaskResponseDto.MapStruct INSTANCE = Mappers.getMapper(ScheduledTaskResponseDto.MapStruct.class);

		ScheduledTaskResponseDto toDto(ScheduledTask requestDto);
	}
}
