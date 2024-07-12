package com.rmf.apiserverjava.global.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.rmf.apiserverjava.global.exception.custom.BusinessException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JwtUtil.
 *
 * JWT 토큰의 생성, 검증, 정보 추출을 담당하는 클래스.
 */
@Component
public class JwtUtil {

	private final CookieUtil cookieUtil;

	/**
	 * TokenType.
	 */
	public enum TokenType {
		ACCESS, REFRESH;
	}

	private static final String USERNAME = "username";
	private static final String ROLE = "role";
	private static final String TOKEN_TYPE = "tokenType";
	private static final String UNHANDLED_TOKEN_TYPE = "UNHANDLED TOKEN TYPE";

	private final ConcurrentHashMap<String, Long> userDormantTime = new ConcurrentHashMap<>();

	private final SecretKey secretKey;
	private final long accessExpiredMs;
	private final long refreshExpiredMs;
	private final String accessTokenName;
	private final String refreshTokenName;

	public JwtUtil(
		@Value("${spring.jwt.secret}") String secret,
		@Value("${spring.jwt.accessExpiredMs}") Long accessExpiredMs,
		@Value("${spring.jwt.refreshExpiredMs}") Long refreshExpiredMs,
		@Value("${spring.jwt.accessTokenName}") String accessTokenName,
		@Value("${spring.jwt.refreshTokenName}") String refreshTokenName,
		CookieUtil cookieUtil) {
		this.accessExpiredMs = accessExpiredMs;
		this.refreshExpiredMs = refreshExpiredMs;
		this.accessTokenName = accessTokenName;
		this.refreshTokenName = refreshTokenName;
		this.cookieUtil = cookieUtil;
		secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm());
	}

	/**
	 * 토큰에서 username을 추출.
	 */
	public String getUsername(String token) {
		return verify()
			.parseSignedClaims(token)
			.getPayload()
			.get(USERNAME, String.class);
	}

	/**
	 * 토큰에서 role을 추출.
	 */
	public String getRole(String token) {
		return verify()
			.parseSignedClaims(token)
			.getPayload()
			.get(ROLE, String.class);
	}

	/**
	 * 토큰에서 TokenType을 추출.
	 */
	public String getTokenType(String token) {
		return verify()
			.parseSignedClaims(token)
			.getPayload()
			.get(TOKEN_TYPE, String.class);
	}

	/**
	 * 토큰이 만료되었는지 확인.
	 */
	public Boolean isExpired(String token) {
		try {
			return verify()
				.parseSignedClaims(token)
				.getPayload()
				.getExpiration()
				.before(new Date(System.currentTimeMillis()));
		} catch (ExpiredJwtException e) {
			return true;
		}
	}

	/**
	 * 토큰의 생성시간을 추적.
	 */
	public long accessGenerateTimeMills(String token) {
		try {
			return verify()
				.parseSignedClaims(token)
				.getPayload()
				.getExpiration().getTime() - accessExpiredMs;
		} catch (JwtException e) {
			return 0;
		}
	}

	/**
	 * JWT 토큰 생성.
	 */
	public String createJwt(String username, String role, TokenType tokenType) {
		long currentTime = System.currentTimeMillis();
		return Jwts.builder()
			.expiration(new Date(currentTime + calcExpiredMs(tokenType)))
			.issuedAt(new Date(currentTime))
			.claim(USERNAME, username)
			.claim(ROLE, role)
			.claim(TOKEN_TYPE, tokenType.name())
			.signWith(secretKey)
			.compact();
	}

	/**
	 * JWT 쿠키 생성.
	 */
	public Cookie createJwtCookie(String username, String role, TokenType tokenType) {
		String jwt = createJwt(username, role, tokenType);
		String tokenName = tokenType == TokenType.ACCESS ? accessTokenName : refreshTokenName;
		String path = tokenType == TokenType.ACCESS ? "/" : "/token/refresh";
		long maxAge = tokenType == TokenType.ACCESS ? accessExpiredMs : refreshExpiredMs;
		return cookieUtil.createCookie(tokenName, jwt, path, (int)maxAge);
	}

	/**
	 * secretKey를 통한 JWT 토큰 검증.
	 */
	private JwtParser verify() {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build();
	}

	/**
	 * TokenType에 따라 만료시간을 계산. 처리하지 않은 TokenType이면 예외 발생.
	 */
	private long calcExpiredMs(TokenType tokenType) {
		if (tokenType == TokenType.ACCESS) {
			return accessExpiredMs;
		} else if (tokenType == TokenType.REFRESH) {
			return refreshExpiredMs;
		} else {
			throw new BusinessException(UNHANDLED_TOKEN_TYPE);
		}
	}

	/**
	 * 쿠키에서 AccessToken 추출.
	 */
	public String getAccessTokenFromCookies(Cookie[] cookies) {
		return cookieUtil.getCookieValue(cookies, accessTokenName);
	}

	/**
	 * 쿠키에서 RefreshToken 추출.
	 */
	public String getRefreshTokenFromCookies(Cookie[] cookies) {
		return cookieUtil.getCookieValue(cookies, refreshTokenName);
	}

	/**
	 * 즉시 만료되는 쿠키를 생성해 AccessToken 만료.
	 */
	public void expireAccessToken(HttpServletResponse response) {
		response.addCookie(cookieUtil.createCookie(accessTokenName, "", "/", 0));
	}

	/**
	 * 즉시 만료되는 쿠키를 생성해 RefreshToekn 만료.
	 */
	public void expireRefreshToken(HttpServletResponse response) {
		response.addCookie(cookieUtil.createCookie(refreshTokenName, "", "/token/refresh", 0));
	}

	/**
	 * AccessToken인지 확인.
	 */
	public boolean isAccessToken(String token) {
		return getTokenType(token).equals(TokenType.ACCESS.name());
	}

	/**
	 * RefreshToken인지 확인.
	 */
	public boolean isRefreshToken(String token) {
		return getTokenType(token).equals(TokenType.REFRESH.name());
	}

	/**
	 * username별 최종 비활성화 시간을 갱신한다
 	 */
	public void renewDormantTime(String username) {
		userDormantTime.put(username, System.currentTimeMillis());
	}

	/**
	 * 토큰이 유효한지 확인한다. TokenType.REFRESH, TokenType.ACCESS가 아닐경우 처리하지 않는다.
	 */
	public boolean isValidToken(String token, TokenType tokenType) {
		try {
			if (token == null || isExpired(token)) {
				return false;
			}
			if (tokenType != TokenType.REFRESH && tokenType != TokenType.ACCESS) {
				return false;
			}
			if (tokenType == TokenType.REFRESH && !isRefreshToken(token)) {
				return false;
			} else if (tokenType == TokenType.ACCESS && !isAccessToken(token)) {
				return false;
			} else if (tokenType == TokenType.ACCESS && accessGenerateTimeMills(token) < userDormantTime.getOrDefault(
				getUsername(token), 0L)) {
				return false;
			}
		} catch (JwtException e) {
			return false;
		}
		return true;
	}

	/**
	 * 쿠키 헤더에서 access token을 추출한다.
	 */
	public String getAccessTokenFromHeader(String cookieHeader) {
		if (cookieHeader != null) {
			String[] cookies = cookieHeader.split(";\\s*");
			for (String cookie : cookies) {
				if (cookie.startsWith(accessTokenName + "=")) {
					return cookie.split("=", 2)[1];
				}
			}
		}
		return null;
	}

	/**
	 * accessToken의 남은 만료시간을 반환한다.
	 */
	public long getTokenExpirationTime(String token) {
		try {
			long expireTime = verify()
				.parseSignedClaims(token)
				.getPayload()
				.getExpiration()
				.getTime();
			return Math.max(0, expireTime - System.currentTimeMillis());
		} catch (JwtException e) {
			return 0;
		}
	}
}
