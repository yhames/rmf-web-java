package com.rmf.apiserverjava.controller;

import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * AuthController.
 *
 * <p>
 *	인증과 관련된 기능을 제공하는 컨트롤러.
 * </p>
 */
public interface AuthController {

	/**
	 * 인증 수단을 만료시켜 로그아웃을 진행
	 */
	ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response);

	/**
	 * 로그인 여부 확인을 진행
	 */
	ResponseEntity<Void> isLogin();
}
