package com.rmf.apiserverjava.entity.fleets;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.rmf.apiserverjava.global.exception.custom.BusinessException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FleetLog entity.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fleetlog")
public class FleetLog {
	@Id
	@Column(name = "name", updatable = false, unique = true, columnDefinition = "VARCHAR(255)")
	private String name;

	@OneToMany(mappedBy = "fleet", orphanRemoval = true, cascade = CascadeType.REMOVE)
	private List<FleetLogLog> logs = new ArrayList<>();

	@OneToMany(mappedBy = "fleet", orphanRemoval = true, cascade = CascadeType.REMOVE)
	private List<FleetLogRobots> robots = new ArrayList<>();

	@Builder
	public FleetLog(String name) {
		this.name = name;
	}
}
