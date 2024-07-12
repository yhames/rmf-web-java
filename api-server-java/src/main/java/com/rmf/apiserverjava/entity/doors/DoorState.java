package com.rmf.apiserverjava.entity.doors;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.rmf.apiserverjava.rosmsgs.door.DoorStateMsg;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@Table(name = "doorstate")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DoorState {

	@Id
	@Column(name = "id", nullable = false, unique = true, columnDefinition = "VARCHAR(255)")
	private String id;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "data", nullable = false, columnDefinition = "json")
	private DoorStateMsg data;

	@Builder
	public DoorState(String id, DoorStateMsg data) {
		this.id = id;
		this.data = data;
	}

	public void updateDoorStateMsg(DoorState doorState) {
		this.data = doorState.getData();
	}
}
