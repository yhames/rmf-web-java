package com.rmf.apiserverjava.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rmf.apiserverjava.entity.fleets.FleetLog;

/**
 * FleetLogRepository.
 *
 */
public interface FleetLogRepository extends JpaRepository<FleetLog, String> {

	/**
	 * PK에 해당하는 FleetLog를 조회한다. 연관된 robots 필드를 엔티티 그래프로 함께 가져온다.
	 */
	@EntityGraph(attributePaths = { "robots" })
	@Query("SELECT fl FROM FleetLog fl WHERE fl.name = :id")
	Optional<FleetLog> findByIdFetchRobots(@Param("id") String id);
}
