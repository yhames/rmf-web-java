package com.rmf.apiserverjava.global.exception.custom;

import lombok.Getter;

/**
 * NotFoundException.
 *
 * <p>
 *     존재하지 않는 요소에 대한 exception.
 * </p>
 */
@Getter
public class NotFoundException extends RuntimeException {
	public NotFoundException(String message) {
		super(message);
	}
}
