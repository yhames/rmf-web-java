package com.rmf.apiserverjava.controller;

import org.springframework.http.ResponseEntity;

import com.rmf.apiserverjava.rosmsgs.buildingmap.BuildingMapMsg;

/**
 * BuildingMapRestController
 */
public interface BuildingMapRestController {

	ResponseEntity<BuildingMapMsg> getBuildingMap();
}
