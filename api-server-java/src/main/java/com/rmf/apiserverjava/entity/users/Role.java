package com.rmf.apiserverjava.entity.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Role entity.
 */

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

	@Id
	@Column(nullable = false, unique = true, columnDefinition = "VARCHAR(255)")
	private String name;
}
