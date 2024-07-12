package com.rmf.apiserverjava.dto.scheduledtasks;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTask;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTaskSchedule;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.exception.custom.InvalidClientArgumentException;
import com.rmf.apiserverjava.rmfapi.tasks.TaskRequestApi;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateScheduledTaskDto {

	@JsonProperty("task_request")
	private TaskRequestApi taskRequest;

	private List<ScheduledTaskScheduleRequestDto> schedules;

	@Builder
	public CreateScheduledTaskDto(TaskRequestApi taskRequest, List<ScheduledTaskScheduleRequestDto> schedules) {
		this.taskRequest = taskRequest;
		this.schedules = schedules;
	}

	@Mapper
	public interface MapStruct {

		String SCHEDULE_NEVER_RUN = "Task is never going to run";

		CreateScheduledTaskDto.MapStruct INSTANCE = Mappers.getMapper(CreateScheduledTaskDto.MapStruct.class);

		@Mapping(source = "task_request", target = "taskRequest")
		CreateScheduledTaskDto toDto(CreateScheduledTaskRequestDto createScheduledTaskRequestDto);

		@Mapping(target = "id", ignore = true)
		@Mapping(target = "exceptDates", ignore = true)
		@Mapping(target = "lastRan", ignore = true)
		@Mapping(source = "dto.taskRequest", target = "taskRequest")
		@Mapping(source = "dto.schedules", target = "schedules", qualifiedByName = "scheduleToEntity")
		@Mapping(source = "user.username", target = "createdBy")
		ScheduledTask toEntity(CreateScheduledTaskDto dto, User user);

		@Named("scheduleToEntity")
		default List<ScheduledTaskSchedule> mapScheduleToEntity(List<ScheduledTaskScheduleRequestDto> schedules) {
			if (schedules == null) {
				throw new InvalidClientArgumentException(SCHEDULE_NEVER_RUN);
			}
			return schedules.stream()
				.map(ScheduledTaskScheduleRequestDto.MapStruct.INSTANCE::toEntity)
				.collect(Collectors.toCollection(CopyOnWriteArrayList::new));
		}
	}
}
