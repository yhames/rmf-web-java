package com.rmf.apiserverjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rmf.apiserverjava.entity.fleets.FleetState;

/**
 * FleetStateRepository.
 *
 */
public interface FleetStateRepository extends JpaRepository<FleetState, String> {
}
