package com.rmf.apiserverjava.controller.impl;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rmf.apiserverjava.controller.TokenRestController;
import com.rmf.apiserverjava.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * TokenJwtRestControllerImpl.
 *
 * <p>
 *	TokenController의 구현체
 * </p>
 */
@Tag(name = "TokenJwt")
@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenJwtRestControllerImpl implements TokenRestController {
	private final JwtService jwtService;

	@Override
	@Operation(summary = "Post Access JWT", description = "쿠키로 전달받은 RefreshToken을 바탕으로 새로운 AccessToken을 쿠키로 발급합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "새로운 AccessToken 발급을 성공했습니다."),
		@ApiResponse(responseCode = "400", description = "새로운 AccessToken 발급을 실패했습니다.")
	})
	@PostMapping("/refresh")
	public ResponseEntity<Void> getNewAccess(HttpServletRequest request, HttpServletResponse response) {
		Optional<Cookie> newToken = jwtService.renewAccessTokenCookie(request, response);

		if (newToken.isPresent()) {
			response.addCookie(newToken.get());
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.badRequest().build();
		}
	}
}
