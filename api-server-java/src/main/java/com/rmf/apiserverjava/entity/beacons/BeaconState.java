package com.rmf.apiserverjava.entity.beacons;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * BeaconState entity.
 */
@Entity
@Table(name = "beaconstate", indexes = {
	@Index(name = "IDX_BEACONSTATE_ONLINE_00", columnList = "online"),
	@Index(name = "IDX_BEACONSTATE_CATEGORY_00", columnList = "category"),
	@Index(name = "IDX_BEACONSTATE_ACTIVATED_00", columnList = "activated"),
	@Index(name = "IDX_BEACONSTATE_LEVEL_00", columnList = "level")
})
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BeaconState {

	@Id
	@Column(updatable = false, unique = true, columnDefinition = "VARCHAR(255)")
	private String id;

	@Column(nullable = false, columnDefinition = "INT")
	private int online;

	@Column(nullable = false, columnDefinition = "VARCHAR(255)")
	private String category;

	@Column(nullable = false, columnDefinition = "INT")
	private int activated;

	@Column(columnDefinition = "VARCHAR(255)")
	private String level;

	public BeaconState updateBeaconState(int online, String category, int activated, String level) {
		this.online = online;
		this.category = category;
		this.activated = activated;
		this.level = level;
		return this;
	}

	@Builder
	public BeaconState(String id, int online, String category, int activated, String level) {
		this.id = id;
		this.online = online;
		this.category = category;
		this.activated = activated;
		this.level = level;
	}
}
