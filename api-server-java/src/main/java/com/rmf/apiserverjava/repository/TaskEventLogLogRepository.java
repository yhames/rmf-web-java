package com.rmf.apiserverjava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rmf.apiserverjava.entity.tasks.TaskEventLogLog;

public interface TaskEventLogLogRepository extends JpaRepository<TaskEventLogLog, Integer> {

	/**
	 * TaskEventLogLog를 TaskEventLog의 pk와 로그 시간 범위로 조회한다.
	 * @param id TaskEventLog의 id
	 * @param start 조회 시작 시간
	 * @param end 조회 종료 시간
	 */
	@EntityGraph(attributePaths = {"task"})
	@Query("SELECT t FROM TaskEventLogLog t WHERE t.task.taskId = :id AND t.unixMillisTime BETWEEN :start AND :end")
	List<TaskEventLogLog> findTaskEventLogLogByIdAndTimeRangeFetch(@Param("id") String id,
		@Param("start") long start, @Param("end") long end);
}
