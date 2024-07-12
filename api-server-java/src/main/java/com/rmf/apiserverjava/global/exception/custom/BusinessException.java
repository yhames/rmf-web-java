package com.rmf.apiserverjava.global.exception.custom;

import lombok.Getter;

/**
 * BusinessException.
 *
 * <p>
 *    내부 로직 처리에 대한 exception.
 * </p>
 */
@Getter
public class BusinessException extends RuntimeException {
	public BusinessException(String message) {
		super(message);
	}
}
