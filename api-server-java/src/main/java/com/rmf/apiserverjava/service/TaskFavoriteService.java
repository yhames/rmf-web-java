package com.rmf.apiserverjava.service;

import java.util.List;

import com.rmf.apiserverjava.dto.tasks.TaskFavoriteDto;
import com.rmf.apiserverjava.entity.tasks.TaskFavorite;

/**
 * TaskFavoriteService.
 *
 * <p>
 *	TaskFavorite와 관련된 비즈니스 로직을 정의하는 서비스.
 * </p>
 */
public interface TaskFavoriteService {

	/**
	 * username에 해당하는 사용자가 즐겨찾기한 TaskFavorite 목록을 조회한다.
	 */
	List<TaskFavorite> getFavoritesTasks(String username);

	/**
	 * TaskFavorite를 즐겨찾기에 추가한다.
	 */
	TaskFavorite postFavoriteTask(TaskFavoriteDto taskFavoriteDto);

	/**
	 * TaskFavorite를 즐겨찾기에서 삭제한다.
	 * 존재하지 않는다면 404 예외를 발생시킨다.
	 * 본인의 TaskFavorite가 아니라면 403 예외를 발생시킨다.
	 */
	void deleteFavoriteTask(String favoriteTaskId, String user);
}
