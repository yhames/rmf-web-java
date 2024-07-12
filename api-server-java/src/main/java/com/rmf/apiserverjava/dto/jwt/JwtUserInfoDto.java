package com.rmf.apiserverjava.dto.jwt;

import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtUserInfoDto {
	private String username;

	@Builder
	public JwtUserInfoDto(String username) {
		this.username = username;
	}
}
