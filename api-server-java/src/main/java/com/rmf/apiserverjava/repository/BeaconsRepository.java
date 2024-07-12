package com.rmf.apiserverjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rmf.apiserverjava.entity.beacons.BeaconState;

/**
 * Repository for beacons using spring Data JPA
 * */
public interface BeaconsRepository extends JpaRepository<BeaconState, String> {

}
