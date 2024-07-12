package com.rmf.apiserverjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rmf.apiserverjava.entity.doors.DoorHealth;

/**
 * DoorHealthRepository
 */
@Repository
public interface DoorHealthRepository extends JpaRepository<DoorHealth, String> {
}
