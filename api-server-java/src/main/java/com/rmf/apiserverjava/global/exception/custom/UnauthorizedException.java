package com.rmf.apiserverjava.global.exception.custom;

import lombok.Getter;

/**
 * UnauthorizedException.
 *
 * <p>
 *    인증이 되지 않았음을 알려주는 exception.
 * </p>
 */
@Getter
public class UnauthorizedException extends RuntimeException {
	public UnauthorizedException(String message) {
		super(message);
	}
}
