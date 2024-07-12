package com.rmf.apiserverjava.dto.tasks;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rmf.apiserverjava.entity.tasks.TaskFavorite;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@Setter
public class TaskFavoriteDto {

	private String id;

	@NotNull
	@NotEmpty
	private String name;

	@JsonProperty("unix_millis_earliest_start_time")
	private Long unixMillisEarliestStartTime;

	private Map<String, Object> priority = new HashMap<>();

	@NotNull
	@NotEmpty
	private String category;

	private Map<String, Object> description = new HashMap<>();

	private String user;

	@Builder
	public TaskFavoriteDto(String id, String name, Long unixMillisEarliestStartTime, Map<String, Object> priority,
		String category, Map<String, Object> description, String user) {
		this.id = id;
		this.name = name;
		this.unixMillisEarliestStartTime = unixMillisEarliestStartTime;
		this.category = category;
		this.user = user;

		if (priority != null) {
			this.priority = priority;
		}
		if (description != null) {
			this.description = description;
		}
	}

	@Mapper
	public interface MapStruct {
		MapStruct INSTANCE = Mappers.getMapper(MapStruct.class);

		@Mapping(source = "unixMillisEarliestStartTime", target = "unixMillisEarliestStartTime",
			qualifiedByName = "timestampToLong")
		TaskFavoriteDto toDto(TaskFavorite taskFavorite);

		@Mapping(source = "unixMillisEarliestStartTime", target = "unixMillisEarliestStartTime",
			qualifiedByName = "longToTimestamp")
		TaskFavorite toEntity(TaskFavoriteDto taskFavoriteDto);

		@Named("longToTimestamp")
		default Timestamp longToTimestamp(Long value) {
			return value != null ? new Timestamp(value) : null;
		}

		@Named("timestampToLong")
		default Long timestampToLong(Timestamp timestamp) {
			return timestamp != null ? timestamp.getTime() : null;
		}
	}
}
