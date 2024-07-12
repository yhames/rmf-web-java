package com.rmf.apiserverjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rmf.apiserverjava.entity.tasks.TaskRequest;

public interface TaskRequestRepository extends JpaRepository<TaskRequest, String> {
}
