package com.rmf.apiserverjava.global.parser;

import org.springframework.stereotype.Component;

import com.rmf.apiserverjava.dto.time.TimeRangeDto;
import com.rmf.apiserverjava.global.exception.custom.InvalidClientArgumentException;

/**
 * LogBetweenParser.
 *
 * <p>
 *     LogBetweenParser 인터페이스의 구현체.
 * </p>
 */
@Component
public class LogBetweenParserImpl implements LogBetweenParser {
	public static String INVALID_ARGUMENT_BETWEEN = "INVALID BETWEEN PARAMETER: ";

	/**
	 * - 와 , 를 기준으로 unixTimeMills로 시간 범위를 파싱한다.
	 * @param between
	 * @return
	 */
	@Override
	public TimeRangeDto parseBetween(String between) {
		try {
			long now = System.currentTimeMillis();
			long start;
			long end;
			if (between.startsWith("-")) {
				long duration = Long.parseLong(between.substring(1));
				start = now - duration;
				end = now;
			} else {
				String[] times = between.split(",");
				start = Long.parseLong(times[0]);
				end = Long.parseLong(times[1]);
			}
			return new TimeRangeDto(start, end);
		} catch (Exception e) {
			throw new InvalidClientArgumentException(INVALID_ARGUMENT_BETWEEN + between);
		}
	}
}
