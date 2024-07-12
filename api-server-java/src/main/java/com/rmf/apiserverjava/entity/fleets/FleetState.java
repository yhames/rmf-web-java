package com.rmf.apiserverjava.entity.fleets;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.rmf.apiserverjava.rmfapi.fleet.FleetStateApi;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FleetState entity.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fleetstate")
@Getter
public class FleetState {
	@Id
	@Column(name = "name", updatable = false, unique = true, columnDefinition = "VARCHAR(255)")
	private String name;

	@Column(name = "data", nullable = false, columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	private FleetStateApi data;

	@Builder
	public FleetState(String name, FleetStateApi data) {
		this.name = name;
		this.data = data;
	}

	/**
	 * FleetStateApi 객체를 전달받아 data 필드를 업데이트
	 */
	public void updateData(FleetStateApi fleetStateApi) {
		this.data = fleetStateApi;
	}
}
