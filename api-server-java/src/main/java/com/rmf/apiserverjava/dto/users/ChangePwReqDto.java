package com.rmf.apiserverjava.dto.users;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangePwReqDto {

	@NotNull
	@NotEmpty
	private String currentPassword;

	@NotNull
	@NotEmpty
	private String newPassword;

	@NotNull
	@NotEmpty
	private String confirmPassword;

	@Builder
	public ChangePwReqDto(String currentPassword, String newPassword, String confirmPassword) {
		this.currentPassword = currentPassword;
		this.newPassword = newPassword;
		this.confirmPassword = confirmPassword;
	}
}
