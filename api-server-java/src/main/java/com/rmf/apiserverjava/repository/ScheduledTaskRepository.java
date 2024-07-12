package com.rmf.apiserverjava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTask;

@Repository
public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, Integer>, ScheduledTaskRepositoryCustom {

	/**
	 * findAll
	 *
	 * <p>
	 *     ScheduledTask의 schedules 정보를 함께 조회합니다.
	 * </p>
	 */
	@Override
	@EntityGraph(attributePaths = { "schedules" })
	List<ScheduledTask> findAll();
}
