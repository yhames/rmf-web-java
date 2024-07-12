package com.rmf.apiserverjava.entity.tasks;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.rmf.apiserverjava.rmfapi.tasks.TaskRequestApi;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TaskRequest entity.
 */

@Entity
@Table(name = "taskrequest")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TaskRequest {

	@Id
	@Column(updatable = false, unique = true, columnDefinition = "VARCHAR(255)")
	private String id;

	@Column(nullable = false, columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	private TaskRequestApi request;

	@Builder
	public TaskRequest(String id, TaskRequestApi request) {
		this.id = id;
		this.request = request;
	}
}
