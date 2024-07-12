package com.rmf.apiserverjava.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rmf.apiserverjava.global.utils.JwtUtil;
import com.rmf.apiserverjava.security.userdetails.CustomUserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JwtAuthFilter.
 *
 * <p>
 *	LoginFilter 이전 유효한 JWT를 전달한 사용자를 인증하는 필터
 * </p>
 */
public class JwtAuthFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;

	public JwtAuthFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	/**
	 * JWT Access 토큰을 검증하고, 유효한 경우 SecurityContext에 인증 정보를 저장
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String authorization = jwtUtil.getAccessTokenFromCookies(request.getCookies());

		if (!jwtUtil.isValidToken(authorization, JwtUtil.TokenType.ACCESS)) {
			jwtUtil.expireAccessToken(response);
			filterChain.doFilter(request, response);
			return;
		}

		String username = jwtUtil.getUsername(authorization);
		String role = jwtUtil.getRole(authorization);
		CustomUserDetails user = new CustomUserDetails(username, null, role);
		Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		filterChain.doFilter(request, response);
	}
}
