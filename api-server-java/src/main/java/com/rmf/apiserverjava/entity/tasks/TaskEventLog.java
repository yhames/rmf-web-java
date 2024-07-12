package com.rmf.apiserverjava.entity.tasks;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TaskEventLog entity.
 */
@Entity
@Table(name = "taskeventlog")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TaskEventLog {

	@Id
	@Column(updatable = false, unique = true, columnDefinition = "VARCHAR(255)")
	@JsonProperty("task_id")
	private String taskId;

	@OneToMany(mappedBy = "task", orphanRemoval = true, cascade = CascadeType.REMOVE)
	private List<TaskEventLogLog> logs = new ArrayList<>();

	@OneToMany(mappedBy = "task", orphanRemoval = true, cascade = CascadeType.REMOVE)
	private List<TaskEventLogPhases> phases = new ArrayList<>();

	@Builder
	public TaskEventLog(String taskId) {
		this.taskId = taskId;
	}
}
