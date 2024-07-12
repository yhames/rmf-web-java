package com.rmf.apiserverjava.service.impl;

import java.util.Set;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTask;
import com.rmf.apiserverjava.global.exception.custom.InvalidClientArgumentException;
import com.rmf.apiserverjava.jobs.ScheduledTaskJob;
import com.rmf.apiserverjava.service.QuartzService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * QuartzServiceImpl
 *
 * <p>
 *     QuartzService 구현체
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuartzServiceImpl implements QuartzService {

	private static final String SCHEDULE_NEVER_RUN = "Task is never going to run";

	private static final String JOB_COUNT = "Quartz Scheduler: Current Job Count: {}";

	private static final String JOB_REGISTER = "Quartz Scheduler: Job을 성공적으로 등록했습니다: Current Job Count: {}";

	private static final String JOB_NOT_FOUND = "Quartz Scheduler: Job Not Found: Scheduled Task Id: {}";

	private static final String JOB_DELETED = "Quartz Scheduler: Job Deleted: Scheduled Task Id: {}";

	private final SchedulerFactoryBean schedulerFactoryBean;

	/**
	 * scheduleTask
	 *
	 * <p>
	 *     ScheduledTask를 스케줄러에 등록합니다.
	 *     don't allow creating scheduled tasks that never runs
	 * </p>
	 */
	@Override
	public void scheduleTask(ScheduledTask scheduledTask) throws SchedulerException {
		Set<Trigger> triggers = scheduledTask.getTriggers();
		if (triggers.isEmpty()) {
			throw new InvalidClientArgumentException(SCHEDULE_NEVER_RUN);
		}
		JobDetail job = JobBuilder.newJob(ScheduledTaskJob.class)
			.usingJobData(ScheduledTaskJob.KEY, scheduledTask.getId())
			.withIdentity(JobKey.jobKey(String.valueOf(scheduledTask.getId()), ScheduledTaskJob.GROUP))
			.build();
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		try {
			scheduler.scheduleJob(job, triggers, true);	// replace existing job with new job
		} catch (SchedulerException e) {
			throw new InvalidClientArgumentException(SCHEDULE_NEVER_RUN);
		}
		log.info(JOB_REGISTER, getScheduledTaskJobCount());
	}

	/**
	 * unScheduleTask
	 *
	 * <p>
	 *     ScheduledTask를 스케줄러에서 삭제합니다.
	 * </p>
	 */
	@Override
	public void unScheduleTask(ScheduledTask scheduledTask) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(String.valueOf(scheduledTask.getId()), ScheduledTaskJob.GROUP);
		if (!scheduler.checkExists(jobKey)) {
			log.info(JOB_NOT_FOUND, jobKey.getName());
			return;
		}
		scheduler.deleteJob(jobKey);
		log.info(JOB_DELETED, jobKey.getName());
		log.info(JOB_COUNT, getScheduledTaskJobCount());
	}

	public int getScheduledTaskJobCount() throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(ScheduledTaskJob.GROUP));
		return jobKeys.size();
	}
}
