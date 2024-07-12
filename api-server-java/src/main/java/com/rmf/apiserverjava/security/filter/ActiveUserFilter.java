package com.rmf.apiserverjava.security.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.rmf.apiserverjava.global.exception.custom.UnauthorizedException;
import com.rmf.apiserverjava.global.utils.JwtUtil;
import com.rmf.apiserverjava.security.UserSession;
import com.rmf.apiserverjava.service.AuthService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * ActiveUserFilter.
 *
 * <p>
 *	JWT 방식의 활성 사용자 세션을 관리하는 필터
 * </p>
 */
public class ActiveUserFilter extends OncePerRequestFilter {
	private static final String SESSION_EXPIRED = "Session Expired";
	private final JwtUtil jwtUtil;
	private final UserSession userSession;
	private final AuthService authService;

	public ActiveUserFilter(JwtUtil jwtUtil, UserSession userSession, AuthService authService) {
		this.jwtUtil = jwtUtil;
		this.userSession = userSession;
		this.authService = authService;
	}

	/**
	 * 사용자의 세션을 관리한다. 유효한 JWT를 전달하였지만 세션이 만료된 경우 로그아웃 처리한다.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String authorization = jwtUtil.getAccessTokenFromCookies(request.getCookies());
		if (jwtUtil.isValidToken(authorization, JwtUtil.TokenType.ACCESS)) {
			String username = jwtUtil.getUsername(authorization);
			if (userSession.isExpired(username)) {
				authService.logout(request, response);
				throw new UnauthorizedException(SESSION_EXPIRED);
			} else {
				userSession.renewSession(username);
			}
		}

		filterChain.doFilter(request, response);
	}
}
