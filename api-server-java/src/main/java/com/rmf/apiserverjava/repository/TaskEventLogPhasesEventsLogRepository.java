package com.rmf.apiserverjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhasesEventsLog;

public interface TaskEventLogPhasesEventsLogRepository extends JpaRepository<TaskEventLogPhasesEventsLog, Integer> {
}
