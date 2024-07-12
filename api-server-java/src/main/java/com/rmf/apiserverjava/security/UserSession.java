package com.rmf.apiserverjava.security;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * UserSession.
 *
 * <p>
 *	최종 요청 시간을 바탕으로 유저의 세션정보를 관리
 * </p>
 */
@Component
public class UserSession {
	private final ConcurrentHashMap<String, Long> userActiveTime = new ConcurrentHashMap<>();

	private final long sessionTimeout;

	public UserSession(@Value("${spring.jwt.sessionExpiredMs}") Long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	/**
	 * 세션 갱신
	 */
	public void renewSession(String username) {
		userActiveTime.put(username, System.currentTimeMillis());
	}

	/**
	 * 세션 삭제
	 */
	public void removeSession(String username) {
		userActiveTime.remove(username);
	}

	/**
	 * 세션 만료 여부 확인
	 */
	public boolean isExpired(String username) {
		Long lastActiveTime = userActiveTime.get(username);
		if (lastActiveTime == null) {
			return true;
		} else {
			return lastActiveTime + sessionTimeout < System.currentTimeMillis();
		}
	}
}
