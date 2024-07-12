package com.rmf.apiserverjava.rmfapi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Tier {
	uninitialized("uninitialized"),
	info("info"),
	warning("warning"),
	error("error");

	private final String value;
}
