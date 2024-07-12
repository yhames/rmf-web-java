package com.rmf.apiserverjava.security;

public enum SecurityRole {
	ROLE_ADMIN("ADMIN"),
	ROLE_USER("USER");

	public final String value;

	SecurityRole(String value) {
		this.value = value;
	}
}
