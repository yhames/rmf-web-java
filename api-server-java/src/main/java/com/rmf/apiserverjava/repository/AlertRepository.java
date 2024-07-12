package com.rmf.apiserverjava.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rmf.apiserverjava.entity.alerts.Alert;

/**
 * AlertRepository.
 *
 * <p>
 *
 * </p>
 */
public interface AlertRepository extends JpaRepository<Alert, String> {
	Optional<Alert> findFirstByOriginalId(String originalId);
}
