package com.rmf.apiserverjava.entity.alerts;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.rmf.apiserverjava.global.exception.custom.BusinessException;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Alert entity.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "alert", indexes = {
	@Index(name = "IDX_ALERT_ORIGINAL_ID_00", columnList = "original_id"),
	@Index(name = "IDX_ALERT_CATEGORY_00", columnList = "category"),
	@Index(name = "IDX_ALERT_UNIX_MILLIS_CREATED_TIME_00", columnList = "unix_millis_created_time"),
	@Index(name = "IDX_ALERT_ACKNOWLEDGED_BY_00", columnList = "acknowledged_by"),
	@Index(name = "IDX_ALERT_UNIX_MILLIS_ACKNOWLEDGED_TIME_00", columnList = "unix_millis_acknowledged_time")
})
@Getter
public class Alert {
	public static final String ACK_ID_PREFIX = "__";
	@Id
	@Column(name = "id", updatable = false, unique = true, columnDefinition = "VARCHAR(255)")
	private String id;

	@Column(name = "original_id", nullable = false, columnDefinition = "VARCHAR(255)")
	private String originalId;

	@Convert(converter = CategoryConverter.class)
	@Column(name = "category", nullable = false, columnDefinition = "VARCHAR(7)")
	private Category category;

	@Column(name = "unix_millis_created_time", nullable = false, columnDefinition = "BIGINT")
	private long unixMillisCreatedTime;

	@Column(name = "acknowledged_by", columnDefinition = "VARCHAR(255)")
	private String acknowledgedBy;

	@Column(name = "unix_millis_acknowledged_time", columnDefinition = "BIGINT")
	private Long unixMillisAcknowledgedTime;

	@Builder
	public Alert(String id, String originalId, Category category, long unixMillisCreatedTime) {
		this.id = id;
		this.originalId = originalId;
		this.category = category;
		this.unixMillisCreatedTime = unixMillisCreatedTime;
	}

	/**
	 * Alert의 Category 및 unixMillisCreatedTime 정보를 업데이트한다.
	 */
	public void update(Category category) {
		this.category = category;
		this.unixMillisCreatedTime = System.currentTimeMillis();
	}

	/**
	 * Alert를 복제한다. pk는 전달받은 id로 설정한다.
	 */
	public Alert clone() {
		return Alert.builder()
			.originalId(this.originalId)
			.category(this.category)
			.unixMillisCreatedTime(this.unixMillisCreatedTime)
			.build();
	}

	/**
	 * Alert가 인지되었음을 설정한다. id는 originalId + ACK_ID_PREFIX + unixMillisAcknowledgedTime로 설정한다.
	 */
	public Alert acknowledge(long unixMillisAcknowledgedTime, String acknowledgedBy) {
		this.id = this.originalId + ACK_ID_PREFIX + unixMillisAcknowledgedTime;
		this.unixMillisAcknowledgedTime = unixMillisAcknowledgedTime;
		this.acknowledgedBy = acknowledgedBy;
		return this;
	}

	public enum Category {
		DEFAULT("default"),
		TASK("task"),
		FLEET("fleet"),
		ROBOT("robot");

		private final String value;

		Category(String value) {
			this.value = value;
		}

		@JsonValue
		public String getValue() {
			return value;
		}

		/**
		 * String value로부터 Category enum을 Optional로 반환한다.
		 */
		@JsonCreator
		public static Optional<Category> fromValue(String value) {
			for (Category category : Category.values()) {
				if (category.getValue().equals(value)) {
					return Optional.of(category);
				}
			}
			return Optional.empty();
		}
	}

	/**
	 * Category converter.
	 *
	 * <p>
	 *     Alert Category enum에 default를 정의할 수 없기 때문에 사용한다.
	 * </p>
	 */
	@Converter(autoApply = true)
	static class CategoryConverter implements AttributeConverter<Category, String> {
		private static final String NOT_NULL = "CATEGORY CAN'T BE NULL";
		private static final String UNKNOWN_VALUE = "UNKNOWN ALERT CATEGORY VALUE: ";

		/**
		 * DB 저장을 위해 Category enum을 String으로 변환한다.
		 */
		@Override
		public String convertToDatabaseColumn(Alert.Category category) {
			if (category == null) {
				throw new BusinessException(NOT_NULL);
			}
			return category.getValue();
		}

		/**
		 * DB에서 조회된 String을 Category enum으로 변환한다.
		 */
		@Override
		public Alert.Category convertToEntityAttribute(String dbData) {
			if (dbData == null) {
				throw new BusinessException(NOT_NULL);
			}
			return Category.fromValue(dbData).orElseThrow(() -> new BusinessException(UNKNOWN_VALUE + dbData));
		}
	}
}
