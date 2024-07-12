package com.rmf.apiserverjava.dto.users;

import org.testcontainers.shaded.org.checkerframework.checker.units.qual.N;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
public class CreateUserReqDto {

	@NotNull
	@NotEmpty
	private String username;

	@NotNull
	@NotEmpty
	private String email;

	@NotNull
	@NotEmpty
	@JsonProperty("is_admin")
	private Boolean isAdmin;

	@Builder
	public CreateUserReqDto(String username, String email, Boolean isAdmin) {
		this.username = username;
		this.email = email;
		this.isAdmin = isAdmin;
	}
}
