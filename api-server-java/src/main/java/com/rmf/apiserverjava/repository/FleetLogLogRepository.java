package com.rmf.apiserverjava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rmf.apiserverjava.entity.fleets.FleetLogLog;

/**
 * FleetLogLogRepository.
 *
 */
public interface FleetLogLogRepository extends JpaRepository<FleetLogLog, Integer> {

	/**
	 * FleetLogLog를 FleetLog의 pk와 로그 시간 범위로 조회한다.
	 * @param id FleetLog의 PK
	 * @param start 로그 범위 시작 시간
	 * @param end 로그 범위 종료 시간
	 */
	@EntityGraph(attributePaths = {"fleet"})
	@Query("SELECT fll FROM FleetLogLog fll "
		+ "WHERE fll.fleet.name =:id "
		+ "AND fll.unixMillisTime BETWEEN :start AND :end")
	List<FleetLogLog> findFleetLogLogByFleetLogIdAndTimeRangeFetch(@Param("id") String id, @Param("start") long start,
		@Param("end") long end);

	/**
	 * fleetName과 seqList와 일치하는 seq 리스트를 조회한다.
	 * DB에 존재하는 유니크 제약조건 인덱스 UIDX_FLEETLOGLOG_FLEET_ID_SEQ_00 사용한다.
	 */
	@Query("SELECT f.seq FROM FleetLogLog f WHERE f.fleet.name = :name AND f.seq IN :seqList")
	List<Integer> findSeqByFleetIdAndSeqIn(@Param("name") String name, @Param("seqList") List<Integer> seqList);
}
