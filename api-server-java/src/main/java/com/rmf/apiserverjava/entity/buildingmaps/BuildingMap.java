package com.rmf.apiserverjava.entity.buildingmaps;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.rmf.apiserverjava.rosmsgs.buildingmap.BuildingMapMsg;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "buildingmap")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BuildingMap {

	@Id
	@Column(name = "id", nullable = false, unique = true, columnDefinition = "VARCHAR(255)")
	private String id;

	@Column(name = "data", nullable = false, columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	private BuildingMapMsg data;

	@Builder
	public BuildingMap(String id, BuildingMapMsg data) {
		this.id = id;
		this.data = data;
	}
}

