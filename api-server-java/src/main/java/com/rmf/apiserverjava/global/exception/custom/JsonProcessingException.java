package com.rmf.apiserverjava.global.exception.custom;

import lombok.Getter;

/**
 * JsonMappingException
 *
 * <p>
 *     Exception for failed to convert value to type using ObjectMapper.
 * </p>
 */
@Getter
public class JsonProcessingException extends RuntimeException {

	public JsonProcessingException(String message) {
		super(message);
	}
}
