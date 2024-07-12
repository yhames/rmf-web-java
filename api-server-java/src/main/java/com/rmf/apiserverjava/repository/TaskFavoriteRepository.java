package com.rmf.apiserverjava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rmf.apiserverjava.entity.tasks.TaskFavorite;

/**
 * TaskFavoriteRepository.
 */
public interface TaskFavoriteRepository extends JpaRepository<TaskFavorite, String> {
	List<TaskFavorite> findByUser(String username);
}
