package com.rmf.apiserverjava.entity.alerts;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.rmf.apiserverjava.config.annotation.IntegrationTest;
import com.rmf.apiserverjava.global.exception.custom.BusinessException;

// AlertCategoryConverter 통합 테스트를 위해 추가
@IntegrationTest
public class AlertCategoryConverterIntegrationTest {

	@Test
	void ifDbDataNullBusinessException() {
		//Arrange
		String dbData = null;

		//Act
		//Assert
		assertThrows(BusinessException.class,
			() -> new Alert.CategoryConverter().convertToEntityAttribute(dbData));
	}

	@ParameterizedTest
	@ValueSource(strings = {"default1", "task2", "fleet3", "robot4"})
	void ifDbDatNotMatchBusinessException(String data) {
		//Arrange
		String dbData = data;

		//Act
		//Assert
		assertThrows(BusinessException.class,
			() -> new Alert.CategoryConverter().convertToEntityAttribute(dbData));
	}
}
