package com.rmf.apiserverjava.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * AuthService.
 *
 * <p>
 *	인증과 관련된 비즈니스 로직을 처리하는 서비스.
 * </p>
 */
public interface AuthService {

	/**
	 * 인증 수단을 만료시켜 로그아웃을 진행
	 */
	void logout(HttpServletRequest request, HttpServletResponse response);
}
