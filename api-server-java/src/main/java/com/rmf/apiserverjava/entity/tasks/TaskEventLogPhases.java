package com.rmf.apiserverjava.entity.tasks;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TaskEventLogPhases entity.
 */
@Entity
@Table(name = "taskeventlogphases", indexes = {
	@Index(name = "FK_TASKEVENTLOGPHASES_TASK_ID", columnList = "task_id"),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class TaskEventLogPhases {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, unique = true, columnDefinition = "INT")
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_id", nullable = false, columnDefinition = "VARCHAR(255)")
	private TaskEventLog task;

	@Column(nullable = false, columnDefinition = "VARCHAR(255)")
	private String phase;

	@OneToMany(mappedBy = "phase", orphanRemoval = true, cascade = CascadeType.REMOVE)
	private List<TaskEventLogPhasesEvents> events = new ArrayList<>();

	@OneToMany(mappedBy = "phase", orphanRemoval = true, cascade = CascadeType.REMOVE)
	private List<TaskEventLogPhasesLog> logs = new ArrayList<>();

	@Builder
	public TaskEventLogPhases(TaskEventLog task, String phase) {
		this.task = task;
		this.phase = phase;
	}
}
