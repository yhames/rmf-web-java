package com.rmf.apiserverjava.entity.tasks;

import java.sql.Timestamp;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.rmf.apiserverjava.rmfapi.tasks.TaskStateApi;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TaskState entity.
 */

@Entity
@Table(name = "taskstate", indexes = {
	@Index(name = "IDX_TASKSTATE_CATEGORY", columnList = "category"),
	@Index(name = "IDX_TASKSTATE_ASSIGNED_TO", columnList = "assigned_to"),
	@Index(name = "IDX_TASKSTATE_UNIX_MILLIS_START_TIME", columnList = "unix_millis_start_time"),
	@Index(name = "IDX_TASKSTATE_UNIX_MILLIS_FINISH_TIME", columnList = "unix_millis_finish_time"),
	@Index(name = "IDX_TASKSTATE_STATUS", columnList = "status"),
	@Index(name = "IDX_TASKSTATE_UNIX_MILLIS_REQUEST_TIME", columnList = "unix_millis_request_time"),
	@Index(name = "IDX_TASKSTATE_REQUESTER", columnList = "requester"),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TaskState {

	@Id
	@Column(updatable = false, unique = true, columnDefinition = "VARCHAR(255)")
	private String id;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(nullable = false, columnDefinition = "jsonb")
	private TaskStateApi data;

	@Column(columnDefinition = "VARCHAR(255)")
	private String category;

	@Column(columnDefinition = "VARCHAR(255)")
	private String assignedTo;

	@Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private Timestamp unixMillisStartTime;

	@Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private Timestamp unixMillisFinishTime;

	@Column(columnDefinition = "VARCHAR(255)")
	private String status;

	@Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private Timestamp unixMillisRequestTime;

	@Column(columnDefinition = "VARCHAR(255)")
	private String requester;

	private Timestamp timeConverter(Long unixMillis) {
		if (unixMillis == null) {
			return null;
		}
		return new Timestamp(unixMillis / 1000 * 1000);
	}

	public void updateTaskState(TaskStateApi taskStateApi) {

		this.data = taskStateApi;
		this.category = taskStateApi.getCategory().getCategory();
		this.assignedTo = taskStateApi.getAssignedTo().getName();
		this.unixMillisStartTime = timeConverter(taskStateApi.getUnixMillisStartTime());
		this.unixMillisFinishTime = timeConverter(taskStateApi.getUnixMillisFinishTime());
		this.status = taskStateApi.getStatus().getValue();
		this.unixMillisRequestTime = timeConverter(taskStateApi.getBooking().getUnixMillisRequestTime());
		this.requester = taskStateApi.getBooking().getRequester();
	}

	@Builder
	public TaskState(String id, TaskStateApi data, String category,
		String assignedTo, Long unixMillisStartTime,
		Long unixMillisFinishTime, String status,
		Long unixMillisRequestTime, String requester) {
		this.id = id;
		this.data = data;
		this.category = category;
		this.assignedTo = assignedTo;
		this.unixMillisStartTime = timeConverter(unixMillisStartTime);
		this.unixMillisFinishTime = timeConverter(unixMillisFinishTime);
		this.status = status;
		this.unixMillisRequestTime = timeConverter(unixMillisRequestTime);
		this.requester = requester;
	}
}
