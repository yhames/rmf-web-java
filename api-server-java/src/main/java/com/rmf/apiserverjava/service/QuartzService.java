package com.rmf.apiserverjava.service;

import org.quartz.SchedulerException;

import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTask;

/**
 * QuartzService
 *
 * <p>
 *     Quartz를 사용하여 스케줄링을 관리하는 서비스입니다.
 * </p>
 */
public interface QuartzService {

	void scheduleTask(ScheduledTask scheduledTask) throws SchedulerException;

	void unScheduleTask(ScheduledTask scheduledTask) throws SchedulerException;

	int getScheduledTaskJobCount() throws SchedulerException;
}
