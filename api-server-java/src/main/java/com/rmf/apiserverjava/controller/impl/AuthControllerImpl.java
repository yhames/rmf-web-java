package com.rmf.apiserverjava.controller.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rmf.apiserverjava.controller.AuthController;
import com.rmf.apiserverjava.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * AuthControllerImpl.
 *
 * <p>
 *	AuthController의 구현체.
 * </p>
 */
@Tag(name = "Auth")
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {
	private final AuthService authService;

	@Override
	@Operation(summary = "Logout", description = "JWT 쿠키를 만료시키고 DB에 저장된 RefreshToken을 삭제해 로그아웃을 수행합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그아웃에 성공합니다."),
		@ApiResponse(responseCode = "403", description = "권한이 없어 로그아웃에 실패하였습니다")
	})
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
		authService.logout(request, response);
		return ResponseEntity.ok().build();
	}

	@Override
	@Operation(summary = "IsLogin", description = "Authorization JWT 쿠키를 바탕으로 로그인이 되어있는지 확인합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그인이 되어 있습니다."),
		@ApiResponse(responseCode = "403", description = "로그인이 되어 있지 않습니다.")
	})
	@GetMapping("/is_login")
	public ResponseEntity<Void> isLogin() {
		return ResponseEntity.ok().build();
	}
}
