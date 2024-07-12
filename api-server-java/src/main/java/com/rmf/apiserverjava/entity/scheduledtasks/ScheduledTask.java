package com.rmf.apiserverjava.entity.scheduledtasks;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.quartz.Trigger;

import com.rmf.apiserverjava.rmfapi.tasks.TaskRequestApi;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "scheduledtask")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduledTask {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, unique = true, nullable = false, columnDefinition = "INTEGER")
	private int id;

	@Column(name = "task_request", nullable = false, columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	private TaskRequestApi taskRequest;

	@Column(name = "created_by", nullable = false, columnDefinition = "VARCHAR(255)")
	private String createdBy;

	@Column(name = "last_ran", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private Timestamp lastRan;

	@Column(name = "except_dates", columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> exceptDates;

	@OneToMany(mappedBy = "scheduledTask", cascade = CascadeType.REMOVE)
	private List<ScheduledTaskSchedule> schedules;

	@Builder
	public ScheduledTask(String createdBy, List<String> exceptDates, int id, Timestamp lastRan,
		List<ScheduledTaskSchedule> schedules, TaskRequestApi taskRequest) {
		this.createdBy = createdBy;
		this.exceptDates = exceptDates;
		this.id = id;
		this.lastRan = lastRan;
		this.schedules = schedules;
		this.taskRequest = taskRequest;
	}

	/**
	 * 연관관계 편의 메서드 - 호출
	 */
	public void addScheduledTaskSchedule(ScheduledTaskSchedule schedule) {
		this.schedules.stream().filter(s -> s.getUuid().equals(schedule.getUuid()))
			.findAny()
			.ifPresent(s -> this.schedules.remove(s));
		this.schedules.add(schedule);
	}

	public void updateLastRun(LocalDateTime now) {
		this.lastRan = Timestamp.valueOf(now);
	}

	/**
	 * Trigger 목록을 반환합니다.
	 */
	public Set<Trigger> getTriggers() {
		return this.schedules.stream().map(ScheduledTaskSchedule::toTrigger).collect(Collectors.toSet());
	}

	public void update(TaskRequestApi taskRequest, List<ScheduledTaskSchedule> newSchedules) {
		this.taskRequest = taskRequest;
		this.schedules = newSchedules;
	}

	public void addExceptDates(String exceptDate) {
		if (this.exceptDates == null) {
			this.exceptDates = new CopyOnWriteArrayList<>();
		}
		String isoDate = LocalDateTime.parse(exceptDate, DateTimeFormatter.ISO_DATE_TIME)
			.format(DateTimeFormatter.ISO_DATE);
		exceptDates.add(isoDate);	// Date only
	}

	public void resetExceptDates() {
		if (this.exceptDates == null) {
			this.exceptDates = new CopyOnWriteArrayList<>();
		}
		this.exceptDates.clear();
	}
}
