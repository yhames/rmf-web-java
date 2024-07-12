package com.rmf.apiserverjava.security;

import static com.corundumstudio.socketio.AuthorizationResult.*;

import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.AuthorizationResult;
import com.corundumstudio.socketio.HandshakeData;
import com.rmf.apiserverjava.global.utils.JwtUtil;

import lombok.RequiredArgsConstructor;

/**
 * SocketIoAuthorizationListener.
 *
 * <p>
 *	netty 서버의 Socket.io 연결에 대한 인가를 담당하는 클래스
 * </p>
 */
@Component
@RequiredArgsConstructor
public class SocketIoAuthorizationListener implements AuthorizationListener {
	private final JwtUtil jwtUtil;
	private final UserSession userSession;

	/**
	 * 인가 결과를 반환하는 메서드. 유효한 AccessToken이 존재하는지 확인한다.
	 */
	@Override
	public AuthorizationResult getAuthorizationResult(HandshakeData data) {
		String cookie = data.getHttpHeaders().get("Cookie");
		String accessToken = jwtUtil.getAccessTokenFromHeader(cookie);
		if (jwtUtil.isValidToken(accessToken, JwtUtil.TokenType.ACCESS)) {
			String username = jwtUtil.getUsername(accessToken);
			if (userSession.isExpired(username)) {
				return FAILED_AUTHORIZATION;
			} else {
				return SUCCESSFUL_AUTHORIZATION;
			}
		} else {
			return FAILED_AUTHORIZATION;
		}
	}
}
