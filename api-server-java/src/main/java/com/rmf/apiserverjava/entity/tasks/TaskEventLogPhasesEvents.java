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
 * TaskEventLogPhasesEvents entity.
 */
@Entity
@Table(name = "taskeventlogphasesevents", indexes = {
	@Index(name = "FK_TASKEVENTLOGPHASESEVENTS_PHASE_ID", columnList = "phase_id"),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class TaskEventLogPhasesEvents {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, unique = true, columnDefinition = "INT")
	private int id;

	@Column(nullable = false, columnDefinition = "VARCHAR(255)")
	private String event;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "phase_id", nullable = false, columnDefinition = "INT")
	private TaskEventLogPhases phase;

	@OneToMany(mappedBy = "event", orphanRemoval = true, cascade = CascadeType.REMOVE)
	private List<TaskEventLogPhasesEventsLog> logs = new ArrayList<>();

	@Builder
	public TaskEventLogPhasesEvents(String event, TaskEventLogPhases phase, List<TaskEventLogPhasesEventsLog> logs) {
		this.event = event;
		this.phase = phase;
		this.logs = logs;
	}
}
