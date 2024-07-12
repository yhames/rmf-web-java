package com.rmf.apiserverjava.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.utils.JwtUtil;
import com.rmf.apiserverjava.repository.RefreshTokenRepository;
import com.rmf.apiserverjava.repository.UserRepository;
import com.rmf.apiserverjava.security.UserSession;
import com.rmf.apiserverjava.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * AuthServiceImpl.
 *
 * <p>
 *	AuthService의 구현체.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	private final JwtUtil jwtUtil;
	private final UserSession userSession;
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;

	/**
	 * Access, Refresh 쿠키 및 DB에 저장된 RefreshToken을 삭제시켜 로그아웃을 진행한다.
	 */
	@Override
	@Transactional
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		String access = jwtUtil.getAccessTokenFromCookies(request.getCookies());
		userSession.removeSession(jwtUtil.getUsername(access));
		jwtUtil.renewDormantTime(jwtUtil.getUsername(access));
		jwtUtil.expireAccessToken(response);
		jwtUtil.expireRefreshToken(response);
		Optional<User> user = userRepository.findById(jwtUtil.getUsername(access));
		if (user.isPresent()) {
			refreshTokenRepository.deleteByUser(user.get());
		}
	}
}
