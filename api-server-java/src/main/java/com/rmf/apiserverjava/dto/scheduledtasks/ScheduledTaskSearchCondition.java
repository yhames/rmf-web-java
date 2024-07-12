package com.rmf.apiserverjava.dto.scheduledtasks;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.rmf.apiserverjava.global.exception.custom.DateTimeParseException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduledTaskSearchCondition {

	private LocalDateTime startBefore;

	private LocalDateTime untilAfter;

	private Integer limit;

	private Integer offset;

	private String orderBy;

	@Builder
	public ScheduledTaskSearchCondition(LocalDateTime startBefore, LocalDateTime untilAfter, Integer limit,
		Integer offset, String orderBy) {
		this.startBefore = startBefore;
		this.untilAfter = untilAfter;
		this.limit = limit;
		this.offset = offset;
		this.orderBy = orderBy;
	}

	@Mapper
	public interface MapStruct {

		String PARSE_FAILED = "LocalDateTime Parse Failed: ";

		ScheduledTaskSearchCondition.MapStruct INSTANCE = Mappers.getMapper(
			ScheduledTaskSearchCondition.MapStruct.class);

		@Mapping(source = "start_before", target = "startBefore", qualifiedByName = "valueToDate")
		@Mapping(source = "until_after", target = "untilAfter", qualifiedByName = "valueToDate")
		@Mapping(source = "order_by", target = "orderBy")
		ScheduledTaskSearchCondition toDto(ScheduledTaskPaginationDto requestDto);

		@Named("valueToDate")
		default LocalDateTime mapValueToDate(String value) {
			try {
				Instant instant = Instant.parse(value);
				ZonedDateTime zoneDateTime = instant.atZone(ZoneId.of("UTC"));
				return zoneDateTime.toLocalDateTime();
			} catch (DateTimeException | NullPointerException err) {
				throw new DateTimeParseException(PARSE_FAILED + value);
			}
		}
	}
}
