package com.rmf.apiserverjava.service;

import java.util.Optional;

import com.rmf.apiserverjava.entity.buildingmaps.BuildingMap;

/**
 * BuildingMapService
 */
public interface BuildingMapService {

	Optional<BuildingMap> getBuildingMap();

	Optional<BuildingMap> updateOrCreate(BuildingMap buildingMap);
}
