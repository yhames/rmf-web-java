package com.rmf.apiserverjava.baseentity;

import com.rmf.apiserverjava.rmfapi.Tier;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class LogMixin {

	@Column(name = "seq", nullable = false, columnDefinition = "INTEGER")
	protected int seq;

	@Column(name = "unix_millis_time", nullable = false, columnDefinition = "BIGINT")
	protected long unixMillisTime;

	@Enumerated(EnumType.STRING)
	@Column(name = "tier", nullable = false, columnDefinition = "VARCHAR(255)")
	protected Tier tier;

	@Column(name = "text", nullable = false, columnDefinition = "TEXT")
	protected String text;
}
