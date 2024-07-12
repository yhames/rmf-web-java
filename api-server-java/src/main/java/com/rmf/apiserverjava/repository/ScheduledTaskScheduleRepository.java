package com.rmf.apiserverjava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rmf.apiserverjava.dto.scheduledtasks.ScheduledTaskSearchCondition;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTask;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTaskSchedule;

@Repository
public interface ScheduledTaskScheduleRepository extends JpaRepository<ScheduledTaskSchedule, Integer> {

	void deleteAllByScheduledTaskId(int scheduledTaskId);
}
