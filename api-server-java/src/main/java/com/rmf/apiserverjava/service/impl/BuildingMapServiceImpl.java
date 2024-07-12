package com.rmf.apiserverjava.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rmf.apiserverjava.entity.buildingmaps.BuildingMap;
import com.rmf.apiserverjava.global.exception.custom.BusinessException;
import com.rmf.apiserverjava.repository.BuildingMapRepository;
import com.rmf.apiserverjava.service.BuildingMapService;

import lombok.RequiredArgsConstructor;

/**
 * BuildingMapServiceImpl
 *
 * <p>
 *     BuildingMapService의 구현체.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class BuildingMapServiceImpl implements BuildingMapService {

	private static final String BUILDING_MAP_UPDATE_FAILED = "BuildingMap 업데이트에 실패했습니다.";

	private final BuildingMapRepository buildingMapRepository;

	/**
	 * getBuildingMap
	 *
	 * <p>
	 *     건물 맵을 전체 조회하여 첫 번째 값을 반환한다.
	 * </p>
	 */
	@Override
	public Optional<BuildingMap> getBuildingMap() {
		return buildingMapRepository.findAll().stream().findFirst();
	}

	@Override
	@Transactional
	public Optional<BuildingMap> updateOrCreate(BuildingMap buildingMap) {
		try {
			buildingMapRepository.deleteAll();
			buildingMapRepository.save(buildingMap);
			return Optional.of(buildingMap);
		} catch (Exception e) {
			throw new BusinessException(BUILDING_MAP_UPDATE_FAILED);
		}
	}
}
