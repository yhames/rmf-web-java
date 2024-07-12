package com.rmf.apiserverjava.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rmf.apiserverjava.entity.tasks.TaskEventLog;

/**
 * TaskEventLogRepository interface.
 * */
public interface TaskEventLogRepository extends JpaRepository<TaskEventLog, String> {

	/**
	 * taskId에 해당하는 TaskEventLog를 조회한다. 연관된 TaskEventLogPhases를 함께 조회한다.
	 * */
	@EntityGraph(attributePaths = {"phases"})
	@Query("SELECT t FROM TaskEventLog t WHERE t.taskId = :id")
	Optional<TaskEventLog> findByIdFetchPhases(@Param("id") String taskId);
}
