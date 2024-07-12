package com.rmf.apiserverjava.entity.users;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * UserRole entity.
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ValidationCode {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, unique = true, columnDefinition = "INT")
	private int id;

	@Column(nullable = false, columnDefinition = "VARCHAR(255)")
	private String code;

	@Column(name = "expiration_time", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private LocalDateTime expirationTime;
}
