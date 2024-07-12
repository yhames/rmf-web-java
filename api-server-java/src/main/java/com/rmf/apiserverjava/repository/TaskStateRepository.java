package com.rmf.apiserverjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rmf.apiserverjava.entity.tasks.TaskState;

public interface TaskStateRepository extends JpaRepository<TaskState, String> {
}
