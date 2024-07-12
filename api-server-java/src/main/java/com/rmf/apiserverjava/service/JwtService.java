package com.rmf.apiserverjava.service;

import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JwtService.
 *
 * <p>
 *	Jwt와 관련된 비즈니스 로직을 처리하는 서비스 인터페이스
 * </p>
 */
public interface JwtService {

	/**
	 * RefreshToken을 DB에 저장하거나 갱신한다.
	 */
	void createOrUpdateRefreshToken(String token, String username, HttpServletRequest request);

	/**
	 * DB에 존재하는 RefreshToken과 비교하여 새로운 AccessToken을 발급한다.
	 */
	Optional<Cookie> renewAccessTokenCookie(HttpServletRequest request, HttpServletResponse response);
}
