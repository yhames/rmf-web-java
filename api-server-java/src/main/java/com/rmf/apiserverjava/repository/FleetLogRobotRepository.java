package com.rmf.apiserverjava.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rmf.apiserverjava.entity.fleets.FleetLog;
import com.rmf.apiserverjava.entity.fleets.FleetLogRobots;

/**
 * FleetLogRobotRepository.
 *
 */
public interface FleetLogRobotRepository extends JpaRepository<FleetLogRobots, Integer> {
	/**
	 * FleetLogRobots를 FleetLog의 pk와 로그 시간 범위로 조회한다.
	 * @param id FleetLog의 PK
	 * @param start 로그 범위 시작 시간
	 * @param end 로그 범위 종료 시간
	 */
	@EntityGraph(attributePaths = {"fleet", "logs"})
	@Query("SELECT flr FROM FleetLogRobots flr "
		+ "JOIN flr.logs flrl "
		+ "WHERE flr.fleet.name =:id "
		+ "AND flrl.unixMillisTime BETWEEN :start AND :end")
	List<FleetLogRobots> findFleetLogRobotsAndLogsByFleetLogIdAndTimeRangeFetch(@Param("id") String id,
		@Param("start") long start, @Param("end") long end);


	/**
	 * FleetLog와 로봇 이름으로 FleetLogRobots를 조회한다. 유니크 제약조건 UIDX_FLEETLOGROBOTS_FLEET_ID_NAME_00 활용해 단일로 반환한다.
	 */
	Optional<FleetLogRobots> findByFleetAndName(FleetLog fleet, String name);
}

