package com.rmf.apiserverjava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rmf.apiserverjava.entity.tasks.TaskEventLog;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhases;

public interface TaskEventLogPhasesRepository extends JpaRepository<TaskEventLogPhases, Integer> {

	/**
	 * TaskEventLogPhases를 TaskEventLog의 pk로 조회한다.
	 * */
	@EntityGraph(attributePaths = {"events", "task"})
	@Query("SELECT tp FROM TaskEventLogPhases tp WHERE tp.task.taskId = :taskId")
	List<TaskEventLogPhases> findByIdFetchEvents(@Param("taskId") String taskId);

	/**
	* TaskEventLogPhases를 TaskEventLog와 phase로 조회한다.
	* */
	@EntityGraph(attributePaths = {"events", "task"})
	@Query("SELECT tp FROM TaskEventLogPhases tp WHERE tp.task.taskId = :taskId AND tp.phase = :phase")
	List<TaskEventLogPhases> findAllByTaskAndPhase(@Param("taskId") String taskId, @Param("phase") String phase);
}
