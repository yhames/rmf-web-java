package com.rmf.apiserverjava.dto.scheduledtasks;

import java.sql.Timestamp;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTaskSchedule;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduledTaskScheduleRequestDto {

	private Short every;

	@NotNull
	@JsonProperty("start_from")
	private Timestamp startFrom;

	private Timestamp until;

	@NotNull
	private ScheduledTaskSchedule.Period period;

	@NotNull
	@NotEmpty
	private String at;

	@Builder
	public ScheduledTaskScheduleRequestDto(Short every, Timestamp startFrom, Timestamp until,
		ScheduledTaskSchedule.Period period, String at) {
		this.every = every;
		this.startFrom = startFrom;
		this.until = until;
		this.period = period;
		this.at = at;
	}

	@Mapper
	public interface MapStruct {

		ScheduledTaskScheduleRequestDto.MapStruct INSTANCE = Mappers
			.getMapper(ScheduledTaskScheduleRequestDto.MapStruct.class);

		ScheduledTaskScheduleRequestDto toDto(ScheduledTaskSchedule entity);

		@Mapping(target = "id", ignore = true)
		@Mapping(target = "scheduledTask", ignore = true)	// scheduledTask는 직접 매핑해야 합니다.
		ScheduledTaskSchedule toEntity(ScheduledTaskScheduleRequestDto requestDto);
	}
}
