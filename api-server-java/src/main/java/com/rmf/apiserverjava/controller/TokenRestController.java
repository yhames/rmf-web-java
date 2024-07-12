package com.rmf.apiserverjava.controller;

import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * TokenRestController.
 *
 * <p>
 *	Token 관련 요청을 처리하는 컨트롤러 인터페이스
 * </p>
 */
public interface TokenRestController {
	/**
	 * 새로운 AccessToken을 발급받는다.
	 */
	ResponseEntity<Void> getNewAccess(HttpServletRequest request, HttpServletResponse response);
}
