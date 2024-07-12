package com.rmf.apiserverjava.entity.fleets;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FleetLogRobots entity.
 *
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fleetlogrobots", indexes = {
	@Index(name = "FK_FLEETLOGROBOTS_FLEET_ID", columnList = "fleet_id")},
	uniqueConstraints = {
		@UniqueConstraint(name = "UIDX_FLEETLOGROBOTS_FLEET_ID_NAME_00", columnNames = {"fleet_id", "name"})
	}
)
public class FleetLogRobots {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, unique = true, columnDefinition = "INT")
	private Integer id;

	@Column(name = "name", nullable = false, columnDefinition = "VARCHAR(255)")
	private String name;

	@JoinColumn(name = "fleet_id", nullable = false, columnDefinition = "VARCHAR(255)")
	@ManyToOne(fetch = FetchType.LAZY)
	private FleetLog fleet;

	@OneToMany(mappedBy = "robot", orphanRemoval = true, cascade = CascadeType.REMOVE)
	private List<FleetLogRobotsLog> logs = new ArrayList<>();

	@Builder
	public FleetLogRobots(String name) {
		this.name = name;
	}

	/**
	 * 연관관계 동기화를 하지 않고 fleet를 설정한다.
	 */
	public void setFleetLogWithoutRelation(FleetLog fleetLog) {
		this.fleet = fleetLog;
	}
}
