package com.rmf.apiserverjava.dto.scheduledtasks;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTask;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTaskSchedule;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.rmfapi.tasks.TaskRequestApi;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateScheduledTaskDto {

	@JsonProperty("task_request")
	private TaskRequestApi taskRequest;

	private List<ScheduledTaskScheduleRequestDto> schedules;

	@Builder
	public UpdateScheduledTaskDto(TaskRequestApi taskRequest, List<ScheduledTaskScheduleRequestDto> schedules) {
		this.taskRequest = taskRequest;
		this.schedules = schedules;
	}

	@Mapper
	public interface MapStruct {

		UpdateScheduledTaskDto.MapStruct INSTANCE = Mappers.getMapper(UpdateScheduledTaskDto.MapStruct.class);

		@Mapping(source = "task_request", target = "taskRequest")
		UpdateScheduledTaskDto toDto(UpdateScheduledTaskRequestDto createScheduledTaskRequestDto);

		@Mapping(target = "id", ignore = true)
		@Mapping(target = "exceptDates", ignore = true)
		@Mapping(target = "lastRan", ignore = true)
		@Mapping(source = "dto.taskRequest", target = "taskRequest")
		@Mapping(source = "dto.schedules", target = "schedules", qualifiedByName = "scheduleToEntity")
		@Mapping(source = "user.username", target = "createdBy")
		ScheduledTask toEntity(UpdateScheduledTaskDto dto, User user);

		@Named("scheduleToEntity")
		default List<ScheduledTaskSchedule> mapScheduleToEntity(List<ScheduledTaskScheduleRequestDto> schedules) {
			if (schedules == null) {
				return null;
			}
			return schedules.stream()
				.map(ScheduledTaskScheduleRequestDto.MapStruct.INSTANCE::toEntity)
				.collect(Collectors.toList());
		}
	}
}
