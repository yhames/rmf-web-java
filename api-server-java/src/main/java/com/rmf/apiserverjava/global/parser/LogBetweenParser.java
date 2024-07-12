package com.rmf.apiserverjava.global.parser;

import com.rmf.apiserverjava.dto.time.TimeRangeDto;

/**
 * LogBetweenParser.
 *
 * <p>
 *     로그의 시간 범위를 파싱하는 인터페이스.
 * </p>
 */
public interface LogBetweenParser {
	/**
	 * 로그의 시간 범위를 파싱한다.
	 * @throws IllegalArgumentException
	 */
	TimeRangeDto parseBetween(String between);
}
