package com.rmf.apiserverjava.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.rmf.apiserverjava.dto.jwt.JwtUserInfoDto;
import com.rmf.apiserverjava.dto.tasks.TaskFavoriteDto;

/**
 * TaskFavoriteController.
 *
 * <p>
 *	TaskFavorite와 관련된 API를 제공하는 컨트롤러
 * </p>
 */
public interface TaskFavoriteController {
	ResponseEntity<List<TaskFavoriteDto>> getFavoritesTasks(JwtUserInfoDto jwtUserInfoDto);

	ResponseEntity<TaskFavoriteDto> postFavoriteTask(TaskFavoriteDto taskFavoriteDto, JwtUserInfoDto jwtUserInfoDto);

	ResponseEntity<Void> deleteFavoriteTask(String favoriteTaskId, JwtUserInfoDto jwtUserInfoDto);
}
