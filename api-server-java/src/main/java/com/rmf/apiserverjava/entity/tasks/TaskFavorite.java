package com.rmf.apiserverjava.entity.tasks;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.rmf.apiserverjava.dto.tasks.TaskFavoriteDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TaskFavorite entity.
 */

@Entity
@Table(name = "taskfavorite", indexes = {
	@Index(name = "IDX_TASKFAVORITE_NAME_00", columnList = "name"),
	@Index(name = "IDX_TASKFAVORITE_UNIX_MILLIS_EARLIEST_START_TIME_00",
		columnList = "unix_millis_earliest_start_time"),
	@Index(name = "IDX_TASKFAVORITE_CATEGORY_00", columnList = "category"),
	@Index(name = "IDX_TASKFAVORITE_USER", columnList = "user"),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TaskFavorite {

	@Id
	@Column(updatable = false, unique = true, columnDefinition = "VARCHAR(255)")
	private String id = UUID.randomUUID().toString();

	@Column(nullable = false, columnDefinition = "VARCHAR(255)")
	private String name;

	@Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private Timestamp unixMillisEarliestStartTime;

	@Column(nullable = false, columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, Object> priority;

	@Column(nullable = false, columnDefinition = "VARCHAR(255)")
	private String category;

	@Column(nullable = false, columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, Object> description;

	@Column(nullable = false, columnDefinition = "VARCHAR(255)", name = "\"user\"")
	private String user;

	public void update(TaskFavoriteDto taskFavoriteDto) {
		this.name = taskFavoriteDto.getName();
		this.priority = taskFavoriteDto.getPriority();
		this.category = taskFavoriteDto.getCategory();
		this.description = taskFavoriteDto.getDescription();
		this.unixMillisEarliestStartTime = new Timestamp(taskFavoriteDto.getUnixMillisEarliestStartTime());
		this.user = taskFavoriteDto.getUser();
	}

	/**
	 * id와 unixMillisEarliestStartTime은 필드 레벨에서 설정한다.
	 */
	@Builder
	public TaskFavorite(String name, Map<String, Object> priority,
		String category, Map<String, Object> description, String user, Timestamp unixMillisEarliestStartTime) {
		this.name = name;
		this.priority = priority;
		this.category = category;
		this.description = description;
		this.user = user;
		this.unixMillisEarliestStartTime = unixMillisEarliestStartTime;
	}
}
