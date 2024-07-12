package com.rmf.apiserverjava.entity.fleets;

import com.rmf.apiserverjava.baseentity.LogMixin;
import com.rmf.apiserverjava.rmfapi.Tier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FleetLogRobotsLog entity.
 *
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fleetlogrobotslog", indexes = {
	@Index(name = "FK_FLEETLOGROBOTSLOG_ROBOT_ID", columnList = "robot_id"),
	@Index(name = "IDX_FLEETLOGROBOTSLOGG_UNIX_MILLIS_TIME_00", columnList = "unix_millis_time")},
	uniqueConstraints = {
		@UniqueConstraint(name = "UIDX_FLEETLOGROBOTSLOG_ROBOT_ID_SEQ_00", columnNames = {"robot_id", "seq"})
	}
)
@SequenceGenerator(
	name = "FLEETLOGROBOTSLOG_SEQ_GENERATOR",
	sequenceName = "FLEETLOGROBOTSLOG_SEQ",
	allocationSize = 1000
)
public class FleetLogRobotsLog extends LogMixin {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FLEETLOGROBOTSLOG_SEQ_GENERATOR")
	@Column(name = "id", updatable = false, unique = true, columnDefinition = "INT")
	private Integer id;

	@JoinColumn(name = "robot_id", nullable = false, columnDefinition = "INT")
	@ManyToOne(fetch = FetchType.LAZY)
	private FleetLogRobots robot;

	public FleetLogRobotsLog setFleetLogRobotWithoutRelation(FleetLogRobots robot) {
		this.robot = robot;
		return this;
	}

	@Builder
	public FleetLogRobotsLog(int seq, Tier tier, long unixMillisTime, String text) {
		this.seq = seq;
		this.tier = tier;
		this.unixMillisTime = unixMillisTime;
		this.text = text;
	}
}
