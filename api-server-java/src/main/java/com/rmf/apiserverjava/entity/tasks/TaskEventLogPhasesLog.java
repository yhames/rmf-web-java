package com.rmf.apiserverjava.entity.tasks;

import com.rmf.apiserverjava.baseentity.LogMixin;
import com.rmf.apiserverjava.rmfapi.Tier;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TaskEventLogPhasesLog entity.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(name = "taskeventlogphaseslog", indexes = {
	@Index(name = "FK_TASKEVENTLOGPHASESLOG_PHASE_ID", columnList = "phase_id"),
	@Index(name = "IDX_TASKEVENTLOGPHASESLOG_UNIX_MILLIS_TIME_00", columnList = "unix_millis_time")},
	uniqueConstraints = {
	@UniqueConstraint(name = "UIDX_TASKEVENTLOGPHASESLOG_ID_SEQ_00", columnNames = {"id", "seq"})
})
@SequenceGenerator(
	name = "TASKEVENTLOGPHASESLOG_SEQ_GENERATOR",
	sequenceName = "TASKEVENTLOGPHASESLOG_SEQ",
	allocationSize = 1000
)
public class TaskEventLogPhasesLog extends LogMixin {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TASKEVENTLOGPHASESLOG_SEQ_GENERATOR")
	@Column(updatable = false, unique = true, columnDefinition = "INT")
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "phase_id", nullable = false, columnDefinition = "INT")
	private TaskEventLogPhases phase;

	@Builder
	public TaskEventLogPhasesLog(int seq, long unixMillisTime, Tier tier, String text,
		TaskEventLogPhases phase) {
		this.seq = seq;
		this.unixMillisTime = unixMillisTime;
		this.tier = tier;
		this.text = text;
		this.phase = phase;
	}

	/**
	 * 연관관계 동기화를 하지 않고 phaseLog를 설정.
	 */
	public TaskEventLogPhasesLog setTaskEventLogPhasesWithoutRelation(TaskEventLogPhases phase) {
		this.phase = phase;
		return this;
	}
}
