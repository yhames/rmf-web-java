package com.rmf.apiserverjava.controller.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rmf.apiserverjava.controller.UserController;
import com.rmf.apiserverjava.dto.jwt.JwtUserInfoDto;
import com.rmf.apiserverjava.dto.users.ChangePwReqDto;
import com.rmf.apiserverjava.dto.users.UserResponseDto;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.annotation.jwt.JwtUserInfo;
import com.rmf.apiserverjava.global.exception.hadler.GlobalExceptionHandlerAdvice;
import com.rmf.apiserverjava.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "User")
@Controller
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

	private final UserService userService;

	@Override
	@Operation(summary = "Get my Info", description = "내 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "내 정보 조회 성공")
	})
	@GetMapping("")
	public UserResponseDto getMyInfo(@Parameter(hidden = true) @JwtUserInfo JwtUserInfoDto jwtUserInfoDto) {
		User user = userService.getUser(jwtUserInfoDto.getUsername()).get();
		return UserResponseDto.MapStruct.INSTANCE.toDto(user);
	}

	@Override
	@Operation(summary = "Change Password", description = "비밀번호 변경을 요청합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "비밀번호 변경 성공"),
		@ApiResponse(responseCode = "400", description = "비밀번호 변경 실패",
			content = @Content(schema = @Schema(implementation = GlobalExceptionHandlerAdvice.ErrorResponse.class))),
	})
	@PostMapping("/password")
	public ResponseEntity<Void> changeMyPassword(@Parameter(hidden = true) @JwtUserInfo JwtUserInfoDto jwtUserInfoDto,
		@RequestBody ChangePwReqDto changePwReqDto) {
		userService.changePassword(jwtUserInfoDto.getUsername(), changePwReqDto);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Void> changeMyEmail(String before, String after) {
		//TODO Auto-generated method stub
		return null;
	}
}
