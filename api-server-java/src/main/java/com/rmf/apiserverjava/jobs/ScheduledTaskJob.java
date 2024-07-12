package com.rmf.apiserverjava.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import com.rmf.apiserverjava.service.ScheduledTaskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ScheduledTaskJob
 *
 * <p>
 *     ScheduledTask를 실행하는 Job 클래스
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTaskJob implements Job {

	public static final String KEY = "SCHEDULED_TASK_KEY";

	public static final String GROUP = "SCHEDULED_TASK_GROUP";

	private final ScheduledTaskService scheduledTaskService;

	/**
	 * execute
	 *
	 * <p>
	 *     ScheduledTask를 dispatch합니다.
	 * </p>
	 */
	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
		int scheduledTaskId = jobDataMap.getIntValue(ScheduledTaskJob.KEY);
		scheduledTaskService.dispatchScheduledTask(scheduledTaskId);
	}
}
