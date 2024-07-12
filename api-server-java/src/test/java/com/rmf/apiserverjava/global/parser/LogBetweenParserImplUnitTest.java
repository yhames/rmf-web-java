package com.rmf.apiserverjava.global.parser;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.dto.time.TimeRangeDto;
import com.rmf.apiserverjava.global.exception.custom.InvalidClientArgumentException;

@UnitTest
class LogBetweenParserImplUnitTest {
	LogBetweenParserImpl parser = new LogBetweenParserImpl();

	@Nested
	@DisplayName("parseBetween")
	class ParseBetween {
		@Test
		@DisplayName("시작시간은 종료시간보다 인자로 전달받은 시간만큼 적어야 한다.")
		void testPrefix() {
			//Arrange
			String between = "-3600000";

			//Act
			TimeRangeDto result = parser.parseBetween(between);

			//Assert
			assertThat(result.getStartTimeMillis()).isLessThan(result.getEndTimeMillis());
			assertThat(result.getStartTimeMillis() - Long.valueOf(between)).isEqualTo(result.getEndTimeMillis());
		}

		@Test
		@DisplayName("시작시간과 종료시간을 인자로 전달받은 시간으로 설정한다.")
		void testRange() {
			//Arrange
			String start = "1609459200000";
			String end = "1609545599999";

			//Act
			TimeRangeDto result = parser.parseBetween(start + "," + end);

			//Assert
			assertThat(result.getStartTimeMillis()).isEqualTo(Long.valueOf(start));
			assertThat(result.getEndTimeMillis()).isEqualTo(Long.valueOf(end));
		}

		@Test
		@DisplayName("잘못된 형식의 인자를 전달받으면 InvalidClientArgumentException 예외를 발생시킨다.")
		void invalidArgument() {
			assertThrows(InvalidClientArgumentException.class, () -> parser.parseBetween("abc,def"));
		}
	}
}
