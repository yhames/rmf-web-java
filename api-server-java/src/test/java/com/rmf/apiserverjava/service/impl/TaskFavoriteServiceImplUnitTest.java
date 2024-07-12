package com.rmf.apiserverjava.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.rmf.apiserverjava.config.annotation.UnitTest;
import com.rmf.apiserverjava.dto.tasks.TaskFavoriteDto;
import com.rmf.apiserverjava.entity.tasks.TaskFavorite;
import com.rmf.apiserverjava.global.exception.custom.ForbiddenException;
import com.rmf.apiserverjava.global.exception.custom.NotFoundException;
import com.rmf.apiserverjava.repository.TaskFavoriteRepository;

@UnitTest
class TaskFavoriteServiceImplUnitTest {

	@Mock
	TaskFavoriteRepository taskFavoriteRepository;

	@InjectMocks
	TaskFavoriteServiceImpl taskFavoriteServiceImpl;

	@Nested
	@DisplayName("getFavoritesTasks")
	class GetFavoritesTasks {
		@Test
		@DisplayName("조회에 성공할 경우 List<TaskFavorite> 를 반환한다.")
		void getFavoriteTaskSuccess() {
			//Arrange
			String username = "user";
			int size = 10;
			List<TaskFavorite> result = new ArrayList<>();
			for (int i = 0; i < size; i++) {
				result.add(mock(TaskFavorite.class));
			}
			when(taskFavoriteRepository.findByUser(eq(username))).thenReturn(result);

			//Act
			taskFavoriteServiceImpl.getFavoritesTasks(username);

			//Assert
			verify(taskFavoriteRepository).findByUser(username);
			assertThat(result).size().isEqualTo(size);

		}
	}

	@Nested
	@DisplayName("postFavoriteTask")
	class PostFavoriteTask {
		@Test
		@DisplayName("저장에 성공할 경우 TaskFavorite 를 반환한다.")
		void postSuccess() {
			//Arrange
			TaskFavoriteDto dto = mock(TaskFavoriteDto.class);
			TaskFavorite taskFavorite = mock(TaskFavorite.class);
			when(taskFavoriteRepository.save(any())).thenReturn(taskFavorite);

			//Act
			TaskFavorite result = taskFavoriteServiceImpl.postFavoriteTask(dto);

			//Assert
			verify(taskFavoriteRepository).save(any(TaskFavorite.class));
			assertThat(result).isEqualTo(taskFavorite);
		}

		@Test
		@DisplayName("id값이 null인 경우 새로 생성하여 저장한다")
		void postNewTaskFavorite() {
			//Arrange
			TaskFavoriteDto dto = mock(TaskFavoriteDto.class);
			when(dto.getId()).thenReturn(null);
			TaskFavorite taskFavorite = mock(TaskFavorite.class);
			when(taskFavoriteRepository.save(any())).thenReturn(taskFavorite);

			//Act
			taskFavoriteServiceImpl.postFavoriteTask(dto);

			//Assert
			verify(taskFavoriteRepository).save(any(TaskFavorite.class));
		}

		@Test
		@DisplayName("empty id값이 넘어올 경우 새로 생성하여 저장한다")
		void postNewTaskFavoriteEmptyId() {
			//Arrange
			TaskFavoriteDto dto = mock(TaskFavoriteDto.class);
			when(dto.getId()).thenReturn("");
			TaskFavorite taskFavorite = mock(TaskFavorite.class);
			when(taskFavoriteRepository.save(any())).thenReturn(taskFavorite);

			//Act
			taskFavoriteServiceImpl.postFavoriteTask(dto);

			//Assert
			verify(taskFavoriteRepository).save(any(TaskFavorite.class));
		}

		@Test
		@DisplayName("id값이 넘어올 경우 해당 id의 TaskFavorite를 업데이트한다")
		void updateTaskFavorite() {
			//Arrange
			TaskFavoriteDto dto = mock(TaskFavoriteDto.class);
			TaskFavorite taskFavorite = mock(TaskFavorite.class);
			when(dto.getId()).thenReturn("existId");
			when(taskFavoriteRepository.findById(eq("existId")))
				.thenReturn(Optional.of(taskFavorite));
			String userId = "user";
			when(dto.getUser()).thenReturn(userId);
			when(taskFavorite.getUser()).thenReturn(userId);

			//Act
			taskFavoriteServiceImpl.postFavoriteTask(dto);

			//Assert
			verify(taskFavoriteRepository, never()).save(any(TaskFavorite.class));
		}

		@Test
		@DisplayName("id값이 넘어왔지만 본인의 Favorite Task가 아닐 경우 ForbiddenException이 발생한다")
		void updateTaskFavoriteFailedByDifferentUser() {
			//Arrange
			TaskFavoriteDto dto = mock(TaskFavoriteDto.class);
			TaskFavorite taskFavorite = mock(TaskFavorite.class);
			when(dto.getId()).thenReturn("existId");
			when(taskFavoriteRepository.findById(eq("existId")))
				.thenReturn(Optional.of(taskFavorite));
			String userId = "user";
			String differentUser = "differentUser";
			when(dto.getUser()).thenReturn(userId);
			when(taskFavorite.getUser()).thenReturn(differentUser);

			//Act
			//Assert
			assertThatThrownBy(() -> taskFavoriteServiceImpl
				.postFavoriteTask(dto))
				.isInstanceOf(ForbiddenException.class);
		}

		@Test
		@DisplayName("id값이 넘어왔지만 존재하지 않는 경우 404 예외가 발생한다")
		void updateTaskFavoriteFailedByNotExist() {
			//Arrange
			TaskFavoriteDto dto = mock(TaskFavoriteDto.class);
			when(dto.getId()).thenReturn("existId");
			when(taskFavoriteRepository.findById(eq("existId")))
				.thenReturn(Optional.empty());

			//Act
			//Assert
			assertThatThrownBy(() -> taskFavoriteServiceImpl
				.postFavoriteTask(dto))
				.isInstanceOf(NotFoundException.class);
		}
	}

	@Nested
	@DisplayName("deleteFavoriteTask")
	class DeleteFavoriteTask {
		@Test
		@DisplayName("DB에 해당하는 ID의 TaskFavorite가 존재하지 않을 경우 NotFoundException이 발생한다")
		void notExist() {
			//Arrange
			String notExistId = "notExistId";

			//Act
			//Assert
			assertThatThrownBy(() -> taskFavoriteServiceImpl
				.deleteFavoriteTask(notExistId, "user"))
				.isInstanceOf(NotFoundException.class);
		}

		@Test
		@DisplayName("DB에 해당하는 ID의 TaskFavorite가 존재할 경우 삭제에 성공한다")
		void ifExistDeleteSuccess() {
			//Arrange
			String existId = "existId";
			String user = "user";
			TaskFavorite taskFavorite = mock(TaskFavorite.class);
			when(taskFavoriteRepository.findById(eq(existId))).thenReturn(java.util.Optional.of(taskFavorite));
			when(taskFavorite.getUser()).thenReturn(user);

			//Act
			taskFavoriteServiceImpl.deleteFavoriteTask(existId, user);

			//Assert
			verify(taskFavoriteRepository).delete(eq(taskFavorite));
		}

		@Test
		@DisplayName("DB에 해당하는 ID의 TaskFavorite가 존재하지만 본인의 task가 아닌 경우 삭제를 거부한다")
		void ifExistDeleteFailByForbiddenAccess() {
			//Arrange
			String existId = "existId";
			String user = "user";
			String differentUser = "differentUser";
			TaskFavorite taskFavorite = mock(TaskFavorite.class);
			when(taskFavoriteRepository.findById(eq(existId))).thenReturn(java.util.Optional.of(taskFavorite));
			when(taskFavorite.getUser()).thenReturn(user);

			//Act
			assertThatThrownBy(() -> taskFavoriteServiceImpl
				.deleteFavoriteTask(existId, differentUser))
				.isInstanceOf(ForbiddenException.class);
		}
	}
}
