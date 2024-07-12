package com.rmf.apiserverjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rmf.apiserverjava.entity.buildingmaps.BuildingMap;

/**
 * BuildingMapRepository
 */
@Repository
public interface BuildingMapRepository extends JpaRepository<BuildingMap, Long> {
}
