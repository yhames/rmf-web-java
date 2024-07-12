package com.rmf.apiserverjava.dto.users;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserQueryRequestDto {

	String username;

	@JsonProperty("is_admin")
	Boolean isAdmin;

	Integer limit;

	Integer offset;

	@JsonProperty("order_by")
	String orderBy;

	@Builder
	public UserQueryRequestDto(String username, Boolean isAdmin, Integer limit, Integer offset, String orderBy) {
		this.username = username;
		this.isAdmin = isAdmin;
		this.limit = limit;
		this.offset = offset;
		this.orderBy = orderBy;
	}
}
