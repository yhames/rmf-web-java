package com.rmf.apiserverjava.entity.alerts;

import static java.lang.Thread.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.global.exception.custom.BusinessException;

@UnitTest
class AlertUnitTest {
	@Nested
	@DisplayName("Alert")
	class AlertTest {
		@Nested
		@DisplayName("AlertConstructor")
		class Constructor {
			@Nested
			@DisplayName("Alert(String id, String originalId, Category category, long unixMillisCreatedTime)")
			class AlertIdOriginalIdCategoryUnixMillisCreatedTime {
				@Test
				@DisplayName("인자들이 전달되었을 때 객체 생성에 성공한다.")
				void successTest() {
					//Arrange
					//Act
					Alert alert = createAlert();
					//Assert
					assertNotNull(alert);
				}
			}

			@Nested
			@DisplayName("update")
			class Update {
				@ParameterizedTest
				@ValueSource(strings = {"DEFAULT", "TASK", "FLEET", "ROBOT"})
				@DisplayName("CATEGORY가 null이 아니면 update에 성공한다.")
				void updateSuccess(Alert.Category category) throws InterruptedException {
					//Arrange
					Alert alert = createAlert();
					String id = alert.getId();
					String originalId = alert.getOriginalId();
					String acknowledgedBy = alert.getAcknowledgedBy();
					Long unixMillisAcknowledgedTime = alert.getUnixMillisAcknowledgedTime();
					long unixMillisCreatedTime = alert.getUnixMillisCreatedTime();

					//Act
					sleep(10);
					alert.update(category);

					//Assert
					assertThat(alert.getCategory()).isEqualTo(category);
					assertThat(alert.getUnixMillisCreatedTime()).isNotEqualTo(unixMillisCreatedTime);
					assertThat(alert.getId()).isEqualTo(id);
					assertThat(alert.getOriginalId()).isEqualTo(originalId);
					assertThat(alert.getAcknowledgedBy()).isEqualTo(acknowledgedBy);
					assertThat(alert.getUnixMillisAcknowledgedTime()).isEqualTo(unixMillisAcknowledgedTime);
				}
			}

			@Nested
			@DisplayName("clone")
			class Clone {
				@Test
				@DisplayName("clone을 통해 복제한 객체는 originalId, category, unixMillisCreatedTime이 같다.")
				void cloneSuccess() {
					//Arrange
					Alert alert = createAlert();

					//Act
					Alert clonedAlert = alert.clone();

					//Assert
					assertThat(clonedAlert.getCategory()).isEqualTo(alert.getCategory());
					assertThat(clonedAlert.getOriginalId()).isEqualTo(alert.getOriginalId());
					assertThat(clonedAlert.getUnixMillisCreatedTime()).isEqualTo(alert.getUnixMillisCreatedTime());
					assertThat(clonedAlert.getId()).isNull();
					assertThat(clonedAlert.getAcknowledgedBy()).isNull();
					assertThat(clonedAlert.getUnixMillisAcknowledgedTime()).isNull();
				}
			}

			@Nested
			@DisplayName("acknowledge")
			class Acknowledge {
				@Test
				@DisplayName("acknowledge를 수행하면 전달받은 acknowledgedBy와 unixMillisAcknowledgedTime 필드를 설정한다. "
					+ "id는 originalId + ACK_ID_PREFIX + unixMillisAcknowledgedTime로 설정된다.")
				void acknowledgeSuccess() {
					//Arrange
					Alert alert = createAlert();
					String acknowledgedBy = "username";
					long unixMillisAcknowledgedTime = System.currentTimeMillis();
					String expectedId = alert.getOriginalId() + Alert.ACK_ID_PREFIX + unixMillisAcknowledgedTime;
					//Act

					Alert acknowledge = alert.acknowledge(unixMillisAcknowledgedTime, acknowledgedBy);

					//Assert
					assertThat(acknowledge.getCategory()).isEqualTo(alert.getCategory());
					assertThat(acknowledge.getOriginalId()).isEqualTo(alert.getOriginalId());
					assertThat(acknowledge.getUnixMillisCreatedTime()).isEqualTo(alert.getUnixMillisCreatedTime());
					assertThat(acknowledge.getId()).isEqualTo(expectedId);
					assertThat(acknowledge.getAcknowledgedBy()).isEqualTo(acknowledgedBy);
					assertThat(acknowledge.getUnixMillisAcknowledgedTime()).isEqualTo(unixMillisAcknowledgedTime);
				}
			}
		}
	}

	@Nested
	@DisplayName("CategoryConverter")
	class CategoryConverter {

		@Nested
		@DisplayName("ConvertToDatabaseColumn")
		class ConvertToDatabaseColumn {

			@Test
			@DisplayName("category가 null이면 BusinessException을 발생한다.")
			void ifCategoryNullBusinessException() {
				//Arrange
				Alert.Category category = null;

				//Act
				//Assert
				assertThrows(BusinessException.class,
					() -> new Alert.CategoryConverter().convertToDatabaseColumn(category));
			}

			@ParameterizedTest
			@ValueSource(strings = {"DEFAULT", "TASK", "FLEET", "ROBOT"})
			@DisplayName("category가 null이 아니면 해당 category의 value를 반환한다.")
			void ifCategoryNotNullReturnCategoryValue(Alert.Category category) {
				//Arrange
				Alert.Category expected = category;

				//Act
				String result = new Alert.CategoryConverter().convertToDatabaseColumn(category);

				//Assert
				assertThat(result).isEqualTo(expected.getValue());
			}
		}

		@Nested
		@DisplayName("ConvertToEntityAttribute")
		class ConvertToEntityAttribute {
			@Test
			@DisplayName("dbData가 null이면 BusinessException이 발생한다.")
			void ifDbDataNullBusinessException() {
				//Arrange
				String dbData = null;

				//Act
				//Assert
				assertThrows(BusinessException.class,
					() -> new Alert.CategoryConverter().convertToEntityAttribute(dbData));
			}

			@ParameterizedTest
			@ValueSource(strings = {"default", "task", "fleet", "robot"})
			@DisplayName("존재하는 value 값이면 해당 category 반환한다.")
			void ifDbDataExistReturnCategory(String data) {
				//Arrange
				String dbData = data;

				//Act
				Alert.Category category = new Alert.CategoryConverter().convertToEntityAttribute(dbData);

				//Assert
				assertThat(category.getValue()).isEqualTo(data);
			}

			@ParameterizedTest
			@ValueSource(strings = {"default1", "task2", "fleet3", "robot4"})
			@DisplayName("dbData가 category에 매칭되지 않으면 BusinessException이 발생한다.")
			void ifDbDatNotMatchBusinessException(String data) {
				//Arrange
				String dbData = data;

				//Act
				//Assert
				assertThrows(BusinessException.class,
					() -> new Alert.CategoryConverter().convertToEntityAttribute(dbData));
			}
		}
	}

	private Alert createAlert() {
		String id = UUID.randomUUID().toString();
		return Alert.builder()
			.id(id)
			.originalId(id)
			.category(Alert.Category.DEFAULT)
			.unixMillisCreatedTime(System.currentTimeMillis())
			.build();
	}
}
