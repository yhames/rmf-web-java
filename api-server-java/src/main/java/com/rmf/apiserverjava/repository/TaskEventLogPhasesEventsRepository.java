package com.rmf.apiserverjava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rmf.apiserverjava.entity.tasks.TaskEventLog;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhases;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhasesEvents;

public interface TaskEventLogPhasesEventsRepository extends JpaRepository<TaskEventLogPhasesEvents, Integer> {
	/**
	 * TaskEventLogPhasesEvents를 TaskEventLogPhases의 pk와 로그 시간 범위로 조회한다.
	 * @param id TaskEventLogPhases의 id
	 * @param start 조회 시작 시간
	 * @param end 조회 종료 시간
	 */
	@EntityGraph(attributePaths = {"phase", "logs"})
	@Query("SELECT lpe FROM TaskEventLogPhasesEvents lpe "
		+ "JOIN lpe.logs lpel "
		+ "WHERE lpe.phase.id =:id "
		+ "AND lpel.unixMillisTime BETWEEN :start AND :end")
	List<TaskEventLogPhasesEvents> findLogPhasesEventsLogByLogPhasesIdAndTimeRangeFetch(@Param("id") int id,
		@Param("start") long start, @Param("end") long end);

	/**
	* TaskEventLogPhasesEvents를 TaskEventLogPhases의 pk와 이벤트로 조회한다.
	* */
	@EntityGraph(attributePaths = {"event", "phase"})
	@Query("SELECT tpe FROM TaskEventLogPhasesEvents tpe WHERE tpe.phase.id = :phaseId AND tpe.event = :event")
	List<TaskEventLogPhasesEvents> findAllByPhasesAndEvent(@Param("phaseId") int phaseId, @Param("event") String event);
}
