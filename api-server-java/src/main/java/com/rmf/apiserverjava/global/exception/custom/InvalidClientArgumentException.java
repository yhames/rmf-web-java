package com.rmf.apiserverjava.global.exception.custom;

import lombok.Getter;

/**
 * BusinessException.
 *
 * <p>
 *    클라이언트가 잘못된 인자를 전달한 것에 대한 exception.
 * </p>
 */
@Getter
public class InvalidClientArgumentException extends IllegalArgumentException {
	public InvalidClientArgumentException(String message) {
		super(message);
	}
}
