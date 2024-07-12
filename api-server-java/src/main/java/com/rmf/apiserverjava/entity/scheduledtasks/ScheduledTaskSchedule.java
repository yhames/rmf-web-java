package com.rmf.apiserverjava.entity.scheduledtasks;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

import org.quartz.CronScheduleBuilder;
import org.quartz.ScheduleBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@Table(name = "scheduledtaskschedule", indexes = {
	@Index(name = "FK_SCHEDULEDTASKSCHEDULE_SCHEDULED_TASK_ID", columnList = "scheduled_task_id"),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduledTaskSchedule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, unique = true, nullable = false, columnDefinition = "INTEGER")
	private int id;

	/**
	 * 연관관계 편의 메서드를 위한 UUID
	 */
	@Column(name = "uuid", nullable = false, columnDefinition = "VARCHAR(255)")
	private final String uuid = UUID.randomUUID().toString();

	@Column(name = "every", columnDefinition = "SMALLINT")
	private Short every;

	@Column(name = "start_from", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private Timestamp startFrom;

	@Column(name = "until", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private Timestamp until;

	@Enumerated(EnumType.STRING)
	@Column(name = "period", nullable = false, columnDefinition = "VARCHAR(255)")
	private Period period;

	@Column(name = "at", columnDefinition = "VARCHAR(255)")
	private String at;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "scheduled_task_id", nullable = false, columnDefinition = "INTEGER")
	private ScheduledTask scheduledTask;

	@Builder
	public ScheduledTaskSchedule(int id, Short every, Timestamp startFrom, Timestamp until, Period period, String at,
		ScheduledTask scheduledTask) {
		this.id = id;
		this.every = every;
		this.startFrom = startFrom;
		this.until = until;
		this.period = period;
		this.at = at;
		this.scheduledTask = scheduledTask;
	}

	/**
	 * 연관관계 편의 메서드
	 */
	public void setScheduledTaskWith(ScheduledTask scheduledTask) {
		this.scheduledTask = scheduledTask;
		this.scheduledTask.addScheduledTaskSchedule(this);
	}

	/**
	 * ScheduledTaskSchedule을 Trigger로 변환합니다.
	 */
	public Trigger toTrigger() {
		ScheduleBuilder<?> scheduleBuilder = this.getScheduleBuilder();
		return TriggerBuilder.newTrigger()
			.startAt(startFrom)
			.withSchedule(scheduleBuilder)
			.endAt(until)
			.build();
	}

	/**
	 * ScheduledTaskSchedule의 프로퍼티를 기반으로 ScheduleBuilder를 반환합니다.
	 */
	private ScheduleBuilder<?> getScheduleBuilder() {
		int every = this.every == null ? 1 : this.every;
		String[] hourAndMinute = at.split(":");
		int hour = Integer.parseInt(hourAndMinute[0]);
		int minute = Integer.parseInt(hourAndMinute[1]);

		if (period == Period.hour) {
			return SimpleScheduleBuilder.repeatHourlyForever(every);
		} else if (period == Period.minute) {
			return SimpleScheduleBuilder.repeatMinutelyForever(every);
		} else if (period == Period.day) {
			return CronScheduleBuilder.dailyAtHourAndMinute(hour, minute);
		} else {    // period이 요일인 경우
			return CronScheduleBuilder.weeklyOnDayAndHourAndMinute(period.getDayOfWeek(), hour, minute);
		}
	}

	@Getter
	@RequiredArgsConstructor
	public enum Period {
		monday("monday", Calendar.MONDAY),
		tuesday("tuesday", Calendar.TUESDAY),
		wednesday("wednesday", Calendar.WEDNESDAY),
		thursday("thursday", Calendar.THURSDAY),
		friday("friday", Calendar.FRIDAY),
		saturday("saturday", Calendar.SATURDAY),
		sunday("sunday", Calendar.SUNDAY),
		day("day", -1),
		hour("hour", -1),
		minute("minute", -1);

		private final String value;

		private final int dayOfWeek;
	}
}
