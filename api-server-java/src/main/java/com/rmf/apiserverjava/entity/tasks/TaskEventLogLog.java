package com.rmf.apiserverjava.entity.tasks;

import com.rmf.apiserverjava.baseentity.LogMixin;
import com.rmf.apiserverjava.rmfapi.Tier;

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
 * TaskEventLogLog entity.
 */
@Entity
@Table(name = "taskeventloglog", indexes = {
	@Index(name = "FK_TASKEVENTLOGLOG_TASK_ID", columnList = "task_id"),
	@Index(name = "IDX_TASKEVENTLOGLOG_UNIX_MILLIS_TIME_00", columnList = "unix_millis_time")}, uniqueConstraints = {
	@UniqueConstraint(name = "UIDX_TASKEVENTLOGLOG_TASK_ID_SEQ_00", columnNames = {"task_id", "seq"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SequenceGenerator(
	name = "TASKEVENTLOGLOG_SEQ_GENERATOR",
	sequenceName = "TASKEVENTLOGLOG_SEQ",
	allocationSize = 1000
)
public class TaskEventLogLog extends LogMixin {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TASKEVENTLOGLOG_SEQ_GENERATOR")
	@Column(updatable = false, unique = true, columnDefinition = "INT")
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_id", nullable = false, columnDefinition = "VARCHAR(255)")
	private TaskEventLog task;

	@Builder
	public TaskEventLogLog(int seq, Tier tier, long unixMillisTime, String text) {
		this.seq = seq;
		this.tier = tier;
		this.unixMillisTime = unixMillisTime;
		this.text = text;
	}

	/**
	 * 연관관계 동기화를 하지 않고 taskLog를 설정.
	 */
	public TaskEventLogLog setTaskEventLogWithoutRelation(TaskEventLog task) {
		this.task = task;
		return this;
	}

}
