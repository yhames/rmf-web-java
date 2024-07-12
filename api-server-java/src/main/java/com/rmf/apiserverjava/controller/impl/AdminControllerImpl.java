package com.rmf.apiserverjava.controller.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rmf.apiserverjava.controller.AdminController;
import com.rmf.apiserverjava.dto.users.CreateUserReqDto;
import com.rmf.apiserverjava.dto.users.UserQueryRequestDto;
import com.rmf.apiserverjava.dto.users.UserResponseDto;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.exception.custom.NotFoundException;
import com.rmf.apiserverjava.global.exception.hadler.GlobalExceptionHandlerAdvice;
import com.rmf.apiserverjava.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin")
@Controller
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminControllerImpl implements AdminController {

	private final UserService userService;
	private static final String USER_NOT_FOUND_MSG = "User %s not found";

	@Override
	@Operation(summary = "Get User list", description = "유저 이름의 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "유저 이름 목록 조회 성공"),
	})
	@GetMapping("/users")
	public List<String> getAllUsers(
		@RequestParam(name = "username", required = false) String username,
		@RequestParam(name = "is_admin", required = false) Boolean isAdmin,
		@RequestParam(name = "limit", required = false) Integer limit,
		@RequestParam(name = "offset", required = false) Integer offset,
		@RequestParam(name = "order_by", required = false) String orderBy
	) {
		UserQueryRequestDto userQueryRequestDto = UserQueryRequestDto.builder()
			.username(username)
			.isAdmin(isAdmin)
			.limit(limit)
			.offset(offset)
			.orderBy(orderBy)
			.build();
		List<User> allUsers = userService.getAllUsers(userQueryRequestDto);
		List<String> usernameList = allUsers.stream()
			.map(User::getUsername)
			.collect(Collectors.toList());
		return usernameList;
	}

	@Override
	@Operation(summary = "Get User", description = "유저 아이디로 유저 데이터를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "유저 조회 성공"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
			content = @Content(schema = @Schema(implementation = GlobalExceptionHandlerAdvice.ErrorResponse.class)))
	})
	@GetMapping("/users/{username}")
	public UserResponseDto getUser(@PathVariable String username) {
		User user = userService.getUser(username)
			.orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND_MSG, username)));
		return UserResponseDto.MapStruct.INSTANCE.toDto(user);
	}

	@Override
	@Operation(summary = "Post User", description = "새로운 User를 생성합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "USER 생성 성공"),
		@ApiResponse(responseCode = "400", description = "아이디 중복 등의 이유로 USER 생성 실패",
			content = @Content(schema = @Schema(implementation = GlobalExceptionHandlerAdvice.ErrorResponse.class)))
	})
	@PostMapping("/users")
	public ResponseEntity<UserResponseDto> createUser(@RequestBody CreateUserReqDto createUserReqDto) {
		User created = userService.createUser(createUserReqDto);
		return ResponseEntity.created(null).body(UserResponseDto.MapStruct.INSTANCE.toDto(created));
	}

	@Override
	@Operation(summary = "Delete User", description = "해당 유저를 삭제합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "유저 삭제 성공"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
			content = @Content(schema = @Schema(implementation = GlobalExceptionHandlerAdvice.ErrorResponse.class)))
	})
	@DeleteMapping("/users/{username}")
	public ResponseEntity<Void> deleteUser(@PathVariable String username) {
		userService.deleteUser(username);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/roles")
	public List<String> getUserRoles() {
		return new ArrayList<>();
	}
}
