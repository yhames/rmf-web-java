package com.rmf.apiserverjava.dto.scheduledtasks;

import java.sql.Timestamp;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTask;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTaskSchedule;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduledTaskScheduleResponseDto {

	private Short every;

	@JsonProperty("start_from")
	private Timestamp startFrom;

	private Timestamp until;

	private ScheduledTaskSchedule.Period period;

	private String at;

	@Builder
	public ScheduledTaskScheduleResponseDto(Short every, Timestamp startFrom, Timestamp until,
		ScheduledTaskSchedule.Period period, String at) {
		this.every = every;
		this.startFrom = startFrom;
		this.until = until;
		this.period = period;
		this.at = at;
	}

	@Mapper
	public interface MapStruct {

		ScheduledTaskScheduleResponseDto.MapStruct INSTANCE = Mappers
			.getMapper(ScheduledTaskScheduleResponseDto.MapStruct.class);

		ScheduledTaskScheduleResponseDto toDto(ScheduledTaskSchedule entity);

		@Mapping(target = "id", ignore = true)
		@Mapping(target = "scheduledTask", ignore = true)
		ScheduledTaskSchedule toEntity(ScheduledTaskScheduleResponseDto requestDto);
	}
}