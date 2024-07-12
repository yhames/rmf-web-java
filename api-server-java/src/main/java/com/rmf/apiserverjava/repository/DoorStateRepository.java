package com.rmf.apiserverjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rmf.apiserverjava.entity.doors.DoorState;

/**
 * DoorStateRepository
 */
@Repository
public interface DoorStateRepository extends JpaRepository<DoorState, String> {
}
