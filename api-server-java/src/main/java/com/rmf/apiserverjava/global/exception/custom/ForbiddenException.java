package com.rmf.apiserverjava.global.exception.custom;

import lombok.Getter;

/**
 * ForbiddenException.
 *
 * <p>
 *    금지된 접근에 대한 exception.
 * </p>
 */
@Getter
public class ForbiddenException extends RuntimeException {
	public ForbiddenException(String message) {
		super(message);
	}
}
