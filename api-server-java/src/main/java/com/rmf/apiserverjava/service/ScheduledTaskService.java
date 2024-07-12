package com.rmf.apiserverjava.service;

import java.util.List;
import java.util.Optional;

import com.rmf.apiserverjava.dto.scheduledtasks.CreateScheduledTaskDto;
import com.rmf.apiserverjava.dto.scheduledtasks.ScheduledTaskSearchCondition;
import com.rmf.apiserverjava.dto.scheduledtasks.UpdateScheduledTaskDto;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTask;
import com.rmf.apiserverjava.entity.users.User;

/**
 * ScheduledTaskService
 */
public interface ScheduledTaskService {

	List<ScheduledTask> getScheduledTasks(ScheduledTaskSearchCondition condition);

	Optional<ScheduledTask> postScheduledTask(CreateScheduledTaskDto condition, User user);

	Optional<ScheduledTask> getScheduledTask(int scheduledTaskId);

	void deleteScheduledTask(int scheduledTaskId);

	ScheduledTask updateScheduledTask(int scheduledTaskId, UpdateScheduledTaskDto updateScheduledTaskDto);

	ScheduledTask updateScheduledTaskSingleEvent(
		int scheduledTaskId, String exceptDate, UpdateScheduledTaskDto updateScheduledTaskDto, User user);

	void clearScheduledTaskEvent(int scheduledTaskId, String eventDate);

	void dispatchScheduledTask(int scheduledTaskId);
}
