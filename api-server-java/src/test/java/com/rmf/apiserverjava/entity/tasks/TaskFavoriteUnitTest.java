package com.rmf.apiserverjava.entity.tasks;

import static java.util.Map.*;

import java.sql.Timestamp;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.dto.tasks.TaskFavoriteDto;

@UnitTest
class TaskFavoriteUnitTest {
	@Nested
	@DisplayName("Constructor")
	class Constructor {
		@Test
		@DisplayName("TaskFavorite(name, priority, category, description, user) 생성시 id가 설정된다.")
		void successWithIdAndUnixMillisEarliestStartTime() {
			//Arrange
			//Act
			TaskFavorite taskFavorite = TaskFavorite.builder()
				.user("testUser")
				.category("testCateory")
				.unixMillisEarliestStartTime(new Timestamp(System.currentTimeMillis()))
				.description(of("testKey", "testValue"))
				.priority(of("testKey", "testValue"))
				.build();

			//Assert
			Assertions.assertThat(taskFavorite.getId()).isNotNull();
			Assertions.assertThat(taskFavorite.getUnixMillisEarliestStartTime()).isNotNull();
		}
	}

	@Nested
	@DisplayName("update")
	class Update {
		@Test
		@DisplayName("TaskFavorite의 필드를 업데이트한다.")
		void updateTaskFavorite() {
			//Arrange
			TaskFavorite taskFavorite = TaskFavorite.builder()
				.user("testUser")
				.category("testCateory")
				.unixMillisEarliestStartTime(new Timestamp(System.currentTimeMillis()))
				.description(of("testKey", "testValue"))
				.priority(of("testKey", "testValue"))
				.build();

			TaskFavoriteDto taskFavoriteDto = TaskFavoriteDto.builder()
				.user("updateUser")
				.category("updateCategory")
				.unixMillisEarliestStartTime(new Timestamp(System.currentTimeMillis()).getTime())
				.description(of("updateKey", "updateValue"))
				.priority(of("updateKey", "updateValue"))
				.build();
			//Act
			taskFavorite.update(taskFavoriteDto);
			//Assert
			Assertions.assertThat(taskFavorite.getUser()).isEqualTo("updateUser");
			Assertions.assertThat(taskFavorite.getCategory()).isEqualTo("updateCategory");
			Assertions.assertThat(taskFavorite.getDescription()).contains(entry("updateKey", "updateValue"));
			Assertions.assertThat(taskFavorite.getPriority()).contains(entry("updateKey", "updateValue"));
		}
	}
}
