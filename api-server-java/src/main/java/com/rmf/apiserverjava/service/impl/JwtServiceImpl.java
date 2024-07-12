package com.rmf.apiserverjava.service.impl;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rmf.apiserverjava.entity.jwt.RefreshToken;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.exception.custom.BusinessException;
import com.rmf.apiserverjava.global.utils.JwtUtil;
import com.rmf.apiserverjava.repository.RefreshTokenRepository;
import com.rmf.apiserverjava.repository.UserRepository;
import com.rmf.apiserverjava.service.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * JwtServiceImpl.
 *
 * <p>
 *	JwtService 인터페이스를 구현한 클래스
 * </p>
 */
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
	public static final String JWT_USER_NOT_EXIST = "USER FOR SAVE JWT NOT EXIST";
	public static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final JwtUtil jwtUtil;

	/**
	 * RefreshToken을 새로 생성하거나 갱신한다. 유저가 존재하지 않는 경우에는 BusinessException을 발생시킨다.
	 * Ip는 보안을 위해 암호화하여 저장한다.
	 */
	@Transactional
	public void createOrUpdateRefreshToken(String token, String username, HttpServletRequest request) {
		User user = userRepository.findById(username).orElseThrow(() -> new BusinessException(JWT_USER_NOT_EXIST));
		String ip = bCryptPasswordEncoder.encode(getIpFromRequest(request));

		Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByUser(user);
		if (existingRefreshToken.isPresent()) {
			existingRefreshToken.get().updateToken(token, ip);
		} else {
			RefreshToken refreshToken = RefreshToken.builder().refreshToken(token).user(user).ip(ip).build();
			refreshTokenRepository.save(refreshToken);
		}
	}

	/**
	 * DB에 저장된 RefreshToken과 일치하고, 해당 유저가 존재하고, IP가 일치하는 경우에만 새로운 AccessToken을 발급한다.
	 */
	@Override
	public Optional<Cookie> renewAccessTokenCookie(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = jwtUtil.getRefreshTokenFromCookies(request.getCookies());
		if (!jwtUtil.isValidToken(refreshToken, JwtUtil.TokenType.REFRESH)) {
			jwtUtil.expireRefreshToken(response);
			return Optional.empty();
		}

		String username = jwtUtil.getUsername(refreshToken);
		Optional<User> user = userRepository.findById(username);
		if (user.isEmpty()) {
			jwtUtil.expireRefreshToken(response);
			return Optional.empty();
		}

		String ip = getIpFromRequest(request);
		String role = jwtUtil.getRole(refreshToken);
		Optional<RefreshToken> savedToken = refreshTokenRepository.findByUser(user.get());
		if (savedToken.isEmpty()
			|| !savedToken.get().getRefreshToken().equals(refreshToken)
			|| !bCryptPasswordEncoder.matches(ip, savedToken.get().getIp())) {
			jwtUtil.expireRefreshToken(response);
			return Optional.empty();
		}

		return Optional.of(jwtUtil.createJwtCookie(username, role, JwtUtil.TokenType.ACCESS));
	}

	/**
	 * request에서 IP를 추출한다. X-Forwarded-For 헤더가 존재하는 경우, 최초 주소인 첫 번째 IP를 추출한다.
	 */
	private String getIpFromRequest(HttpServletRequest request) {
		String xForwardedForHeader = request.getHeader(X_FORWARDED_FOR_HEADER);
		if (xForwardedForHeader == null) {
			return request.getRemoteAddr();
		} else {
			return xForwardedForHeader.split(",")[0];
		}
	}
}
