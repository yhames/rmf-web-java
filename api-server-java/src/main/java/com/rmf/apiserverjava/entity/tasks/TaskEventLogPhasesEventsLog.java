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

/**
 * TaskEventLogPhasesEventsLog entity.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "taskeventlogphaseseventslog", indexes = {
	@Index(name = "FK_TASKEVENTLOGPHASESEVENTSLOG_EVENT_ID", columnList = "event_id"),
	@Index(name = "IDX_TASKEVENTLOGPHASESEVENTSLOG_UNIX_MILLIS_TIME_00", columnList = "unix_millis_time")},
	uniqueConstraints = {
	@UniqueConstraint(name = "UIDX_TASKEVENTLOGPHASESEVENTSLOG_ID_SEQ_00", columnNames = {"id", "seq"})
})
@SequenceGenerator(
	name = "TASKEVENTLOGPHASEEVENTSLOG_SEQ_GENERATOR",
	sequenceName = "TASKEVENTLOGPHASEEVENTSLOG_SEQ",
	allocationSize = 1000
)
public class TaskEventLogPhasesEventsLog extends LogMixin {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TASKEVENTLOGPHASEEVENTSLOG_SEQ_GENERATOR")
	@Column(updatable = false, unique = true, columnDefinition = "INT")
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id", nullable = false, columnDefinition = "INT")
	private TaskEventLogPhasesEvents event;

	/**
	 * 연관관계 동기화를 하지 않고 phaseEventLog를 설정.
	 */
	public TaskEventLogPhasesEventsLog setTaskEventLogPhasesEventsWithoutRelation(TaskEventLogPhasesEvents event) {
		this.event = event;
		return this;
	}

	@Builder
	public TaskEventLogPhasesEventsLog(int seq, long unixMillisTime, Tier tier, String text,
		TaskEventLogPhasesEvents event) {
		this.seq = seq;
		this.unixMillisTime = unixMillisTime;
		this.tier = tier;
		this.text = text;
		this.event = event;
	}
}
