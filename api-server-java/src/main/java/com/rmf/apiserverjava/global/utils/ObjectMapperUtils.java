package com.rmf.apiserverjava.global.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * ObjectMapperUtils
 *
 * <p>
 *     ObjectMapper 싱글톤 객체를 제공하는 유틸리티 클래스.
 * </p>
 */
public class ObjectMapperUtils {

	public static final ObjectMapper MAPPER = newInstance();

	/**
	 * newInstance
	 *
	 * <p>
	 *     ObjectMapper 객체를 생성하여 반환한다.
	 *     프로퍼티 네이밍 전략은 스네이크 케이스로 설정하고,
	 *     private 메서드로 선언하여 외부에서 인스턴스화를 방지한다.
	 * </p>
	 */
	private static ObjectMapper newInstance() {
		return new ObjectMapper()
			.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
	}

	private ObjectMapperUtils() {
		throw new RuntimeException();
	}
}
