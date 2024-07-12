package com.rmf.apiserverjava.repository;

import java.util.List;

import com.rmf.apiserverjava.dto.scheduledtasks.ScheduledTaskSearchCondition;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTask;

/**
 * ScheduledTaskRepositoryCustom
 */
public interface ScheduledTaskRepositoryCustom {

	List<ScheduledTask> searchStartBeforeUntilAfter(ScheduledTaskSearchCondition condition);
}
