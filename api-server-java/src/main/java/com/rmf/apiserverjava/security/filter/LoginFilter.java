package com.rmf.apiserverjava.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.rmf.apiserverjava.global.exception.custom.UnauthorizedException;
import com.rmf.apiserverjava.global.utils.JwtUtil;
import com.rmf.apiserverjava.security.UserSession;
import com.rmf.apiserverjava.security.userdetails.CustomUserDetails;
import com.rmf.apiserverjava.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * LoginFilter.
 *
 * <p>
 *     로그인 전후의 처리를 담당하는 Spring Security 필터
 * </p>
 */
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
	public static final String USER_NOT_FOUND = "USER NOT FOUND";

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final JwtService jwtService;
	private final UserSession userSession;

	public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, JwtService jwtService,
		UserSession userSession) {

		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.jwtService = jwtService;
		this.userSession = userSession;
	}

	/**
	 * 사용자가 제출한 자격 증명을 바탕으로 초기 인증을 시도
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {
		String username = obtainUsername(request);
		String password = obtainPassword(request);

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
		return authenticationManager.authenticate(authToken);
	}

	/**
	 * 인증 성공 시 JWT 토큰을 생성하여 Response 쿠키에 저장
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authResult) throws IOException, ServletException {

		CustomUserDetails customUserDetails = (CustomUserDetails)authResult.getPrincipal();
		String username = customUserDetails.getUsername();
		userSession.renewSession(username);
		String role = authResult.getAuthorities().iterator().next().getAuthority();
		Cookie access = jwtUtil.createJwtCookie(username, role, JwtUtil.TokenType.ACCESS);
		Cookie refresh = jwtUtil.createJwtCookie(username, role, JwtUtil.TokenType.REFRESH);
		jwtService.createOrUpdateRefreshToken(refresh.getValue(), username, request);
		response.addCookie(access);
		response.addCookie(refresh);
	}

	/**
	 * 인증 실패 시 NotFound 예외를 발생
	 */
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException, ServletException {
		throw new UnauthorizedException(USER_NOT_FOUND);
	}
}
