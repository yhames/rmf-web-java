package com.rmf.apiserverjava.entity.fleets;

import com.rmf.apiserverjava.baseentity.LogMixin;
import com.rmf.apiserverjava.rmfapi.Tier;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * FleetLogLog entity.
 *
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fleetloglog", indexes = {
	@Index(name = "FK_FLEETLOGLOG_FLEET_ID", columnList = "fleet_id"),
	@Index(name = "IDX_FLEETLOGLOG_UNIX_MILLIS_TIME_00", columnList = "unix_millis_time")},
	uniqueConstraints = {
		@UniqueConstraint(name = "UIDX_FLEETLOGLOG_FLEET_ID_SEQ_00", columnNames = {"fleet_id", "seq"})
	}
)
@SequenceGenerator(
	name = "FLEETLOGLOG_SEQ_GENERATOR",
	sequenceName = "FLEETLOGLOG_SEQ",
	allocationSize = 1000
)
public class FleetLogLog extends LogMixin {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FLEETLOGLOG_SEQ_GENERATOR")
	@Column(name = "id", updatable = false, unique = true, columnDefinition = "INT")
	private Integer id;

	@JoinColumn(name = "fleet_id", nullable = false, columnDefinition = "VARCHAR(255)")
	@ManyToOne(fetch = FetchType.LAZY)
	private FleetLog fleet;

	@Builder
	public FleetLogLog(int seq, Tier tier, long unixMillisTime, String text) {
		this.seq = seq;
		this.tier = tier;
		this.unixMillisTime = unixMillisTime;
		this.text = text;
	}

	/**
	 * 연관관계 동기화를 하지 않고 FleetLog를 설정.
	 */
	public FleetLogLog setFleetLogWithoutRelation(FleetLog fleet) {
		this.fleet = fleet;
		return this;
	}
}
