package com.rmf.apiserverjava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rmf.apiserverjava.entity.fleets.FleetLogRobotsLog;

/**
 * FleetLogRobotsLogRepository.
 */
public interface FleetLogRobotsLogRepository extends JpaRepository<FleetLogRobotsLog, Integer> {

	/**
	 * robotId와 seqList와 일치하는 seq 리스트를 조회한다.
	 * DB에 존재하는 유니크 제약조건 인덱스 UIDX_FLEETLOGROBOTSLOG_ROBOT_ID_SEQ_00를 사용한다.
	 */
	@Query("SELECT f.seq FROM FleetLogRobotsLog f WHERE f.robot.id = :id AND f.seq IN :seqList")
	List<Integer> findSeqByRobotIdAndSeqIn(@Param("id") Integer id, @Param("seqList") List<Integer> seqList);
}
