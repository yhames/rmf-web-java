package com.rmf.apiserverjava.global.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.rmf.apiserverjava.config.annotation.UnitTest;

@UnitTest
class ObjectMapperUtilsUnitTest {
	@Nested
	@DisplayName("Constructor")
	class Constructor {
		@Test
		@DisplayName("생성자를 호출하면 예외를 던진다. 리플렉션으로도 생성할 수 없도록 한다.")
		void failed() {
			//Arrange
			//Act
			//Assert
			assertThrows(InvocationTargetException.class, () -> {
				java.lang.reflect.Constructor<ObjectMapperUtils> constructor;
				constructor = ObjectMapperUtils.class.getDeclaredConstructor();
				constructor.setAccessible(true);
				constructor.newInstance();
			});
		}
	}
}
