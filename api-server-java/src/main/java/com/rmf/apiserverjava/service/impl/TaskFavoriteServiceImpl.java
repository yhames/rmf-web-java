package com.rmf.apiserverjava.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rmf.apiserverjava.dto.tasks.TaskFavoriteDto;
import com.rmf.apiserverjava.entity.tasks.TaskFavorite;
import com.rmf.apiserverjava.global.exception.custom.ForbiddenException;
import com.rmf.apiserverjava.global.exception.custom.NotFoundException;
import com.rmf.apiserverjava.repository.TaskFavoriteRepository;
import com.rmf.apiserverjava.service.TaskFavoriteService;

import lombok.RequiredArgsConstructor;

/**
 * TaskFavoriteServiceImpl.
 *
 * <p>
 *	TaskFavoriteService의 구현체
 * </p>
 */
@Service
@RequiredArgsConstructor
public class TaskFavoriteServiceImpl implements TaskFavoriteService {
	public static final String FAVORITE_TASK_NOT_FOUND = "Favorite Task with ID %s not found";
	public static final String FORBIDDEN_ACCESS = "Favorite Task with ID %s is not yours";
	private final TaskFavoriteRepository taskFavoriteRepository;

	/**
	 * username에 해당하는 사용자가 즐겨찾기한 TaskFavorite 목록을 조회한다.
	 */
	@Override
	public List<TaskFavorite> getFavoritesTasks(String username) {
		return taskFavoriteRepository.findByUser(username);
	}

	/**
	 * TaskFavorite를 즐겨찾기에 추가한다.
	 * 이미 존재하는 TaskFavorite라면 업데이트한다.
	 */
	@Override
	@Transactional
	public TaskFavorite postFavoriteTask(TaskFavoriteDto taskFavoriteDto) {
		TaskFavorite taskFavorite;
		String dtoId = taskFavoriteDto.getId();
		if (dtoId == null || dtoId.isEmpty()) {
			TaskFavorite newTaskFavorite = TaskFavoriteDto.MapStruct.INSTANCE.toEntity(taskFavoriteDto);
			TaskFavorite saved = taskFavoriteRepository.save(newTaskFavorite);
			taskFavorite = saved;
		} else {
			TaskFavorite exist = taskFavoriteRepository.findById(dtoId)
				.orElseThrow(() -> new NotFoundException(String.format(FAVORITE_TASK_NOT_FOUND, dtoId)));
			if (!exist.getUser().equals(taskFavoriteDto.getUser())) {
				throw new ForbiddenException(String.format(FORBIDDEN_ACCESS, dtoId));
			}
			exist.update(taskFavoriteDto);
			taskFavorite = exist;
		}
		return taskFavorite;
	}

	/**
	 * TaskFavorite를 즐겨찾기에서 삭제한다.
	 * 존재하지 않는다면 404 예외를 발생시킨다.
	 * 본인의 TaskFavorite가 아니라면 403 예외를 발생시킨다.
	 */
	@Override
	@Transactional
	public void deleteFavoriteTask(String favoriteTaskId, String user) {
		TaskFavorite taskFavorite = taskFavoriteRepository.findById(favoriteTaskId)
			.orElseThrow(() -> new NotFoundException(String.format(FAVORITE_TASK_NOT_FOUND, favoriteTaskId)));
		if (!taskFavorite.getUser().equals(user)) {
			throw new ForbiddenException(String.format(FORBIDDEN_ACCESS, favoriteTaskId));
		}
		taskFavoriteRepository.delete(taskFavorite);
	}
}
