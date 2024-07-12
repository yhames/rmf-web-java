package com.rmf.apiserverjava.global.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;

/**
 * CookieUtil.
 *
 * <p>
 *	쿠키 관련 유틸리티 클래스
 * </p>
 */
@Component
public class CookieUtil {

	private final String hostname;
	private final Boolean secure;

	public CookieUtil(
		@Value("${socketio.server.hostname}") String hostname,
		@Value("${cookie.secure}") Boolean secure) {
		this.hostname = hostname;
		this.secure = secure;
	}

	/**
	 * 쿠키에서 특정 키의 값을 가져오는 메서드
	 */
	public String getCookieValue(Cookie[] cookies, String key) {
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (key.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * 쿠키 생성 메서드
	 */
	public Cookie createCookie(String key, String value, String path, int maxAge) {
		Cookie cookie = new Cookie(key, value);
		cookie.setDomain(hostname);
		cookie.setPath(path);
		cookie.setHttpOnly(true);
		cookie.setSecure(secure); //TODO: https를 사용할 경우 true로 변경
		cookie.setMaxAge(maxAge);
		return cookie;
	}
}
