package com.rmf.apiserverjava.dto.users;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rmf.apiserverjava.entity.users.Email;
import com.rmf.apiserverjava.entity.users.Role;
import com.rmf.apiserverjava.entity.users.User;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserResponseDto {

	private String username;

	private String email;

	@JsonProperty("is_admin")
	private Boolean isAdmin;

	private List<Role> roles = new ArrayList<>();

	@Mapper
	public interface MapStruct {
		UserResponseDto.MapStruct INSTANCE = Mappers.getMapper(UserResponseDto.MapStruct.class);

		@Mapping(source = "email", target = "email", qualifiedByName = "emailToEmailName")
		UserResponseDto toDto(User user);

		@Named("emailToEmailName")
		default String emailToEmailName(Email email) {
			return email.getEmail();
		}
	}
}
