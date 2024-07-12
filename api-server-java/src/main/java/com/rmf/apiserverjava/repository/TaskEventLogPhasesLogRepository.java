package com.rmf.apiserverjava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhases;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhasesLog;

public interface TaskEventLogPhasesLogRepository extends JpaRepository<TaskEventLogPhasesLog, Integer> {
	/**
	 * TaskEventLogPhasesLog를 TaskEventLog의 pk와 로그 시간 범위로 조회한다.
	 * @param id TaskEventLogPhases의 id
	 * @param start 조회 시작 시간
	 * @param end 조회 종료 시간
	 */
	@EntityGraph(attributePaths = {"phase"})
	@Query("SELECT lpl FROM TaskEventLogPhasesLog lpl "
		+ "WHERE lpl.phase.id =:id "
		+ "AND lpl.unixMillisTime BETWEEN :start AND :end")
	List<TaskEventLogPhasesLog> findLogPhasesLogByLogPhasesIdAndTimeRangeFetch(@Param("id") int id,
		@Param("start") long start, @Param("end") long end);

}
