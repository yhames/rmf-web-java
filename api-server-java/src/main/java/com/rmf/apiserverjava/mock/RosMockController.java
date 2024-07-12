package com.rmf.apiserverjava.mock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rmf.apiserverjava.entity.buildingmaps.BuildingMap;
import com.rmf.apiserverjava.global.exception.custom.BusinessException;
import com.rmf.apiserverjava.global.exception.custom.NotFoundException;
import com.rmf.apiserverjava.global.utils.ObjectMapperUtils;
import com.rmf.apiserverjava.rosmsgs.buildingmap.BuildingMapMsg;
import com.rmf.apiserverjava.rxjava.eventbus.RmfEvents;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RosMock
 *
 * <p>
 *     ros-mock-server를 대체합니다.
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/mock")
@RequiredArgsConstructor
public class RosMockController {

	private static final String buildingMapPath = "src/main/resources/building_map.json";

	private static final String DUMMY_FAILED = "Failed to initialize Dummy Data: {}";

	private final SchedulerFactoryBean schedulerFactoryBean;

	private final RmfEvents rmfEvents;    // for Test

	/**
	 * This is for Test
	 */
	@GetMapping("/image/{image_name}")
	public ResponseEntity<Resource> getAffineImage(@PathVariable("image_name") String imageName) {
		try {
			Path path = Paths.get("src/main/resources/" + imageName);
			UrlResource urlResource = new UrlResource(path.toUri());
			if (!urlResource.exists() || !urlResource.isReadable()) {
				throw new NotFoundException("Image not found");
			}
			return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(urlResource);
		} catch (Exception e) {
			throw new NotFoundException(e.getMessage());
		}
	}

	/**
	 * This is for Test
	 */
	@PostMapping("/dummy")
	public String postDoorDummyScheduler() {
		try {
			publishBuildingMap();
			addDoorDummyScheduler();
			return "success";
		} catch (Exception e) {
			log.error(DUMMY_FAILED, e.getMessage());
			throw new BusinessException(e.getMessage());
		}
	}

	private void addDoorDummyScheduler() throws SchedulerException {
		JobDetail job = JobBuilder.newJob(DoorDummyJob.class)
			.withIdentity(JobKey.jobKey(DoorDummyJob.KEY, DoorDummyJob.GROUP))
			.build();
		Trigger trigger = TriggerBuilder.newTrigger()
			.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(1))
			.build();
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		if (!scheduler.checkExists(job.getKey())) {
			scheduler.scheduleJob(job, trigger);
		}
	}

	private void publishBuildingMap() throws IOException {
		String res = Files.readString(Paths.get(buildingMapPath));
		BuildingMapMsg buildingMapMsg = ObjectMapperUtils.MAPPER.readValue(res, BuildingMapMsg.class);
		BuildingMap buildingMap = BuildingMapMsg.MapStruct.INSTANCE.toEntity(buildingMapMsg);
		rmfEvents.getBuildingMapEvent().onNext(buildingMap);
	}
}
