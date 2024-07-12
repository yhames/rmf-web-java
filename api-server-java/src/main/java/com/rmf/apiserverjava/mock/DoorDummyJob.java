package com.rmf.apiserverjava.mock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.rmf.apiserverjava.entity.doors.DoorState;
import com.rmf.apiserverjava.global.exception.custom.BusinessException;
import com.rmf.apiserverjava.global.utils.ObjectMapperUtils;
import com.rmf.apiserverjava.rosmsgs.builtin.TimeMsg;
import com.rmf.apiserverjava.rosmsgs.door.DoorStateMsg;
import com.rmf.apiserverjava.rxjava.eventbus.RmfEvents;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DoorDummyJob implements Job {

	public static final String KEY = "DOOR_DUMMY_KEY";

	public static final String GROUP = "DOOR_DUMMY_GROUP";

	private static final String SRC_PATH = "src/main/resources/door_dummy/";

	private static final String DOOR_DUMMY_READ_FAILED = "Failed to read door dummy file: ";

	private static final String DOOR_DUMMY_CONVERT_FAILED = "Failed to convert door state msg: ";

	private final RmfEvents rmfEvents;

	private final List<DoorState> doorDummy = new ArrayList<>();

	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		doorDummy.stream()
			.peek((doorState) -> doorState.getData().setDoorTime(TimeMsg.now()))
			.forEach((doorState) -> rmfEvents.getDoorStateEvent().onNext(doorState));
	}

	@PostConstruct
	private void init() {
		try (Stream<Path> paths = Files.list(Paths.get(SRC_PATH))) {
			paths.map(this::readFromPath)
				.map(this::toDoorStateMsg)
				.forEach(doorDummy::add);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String readFromPath(Path path) {
		try {
			log.info("Reading file: {}", path.toString());
			return Files.readString(path);
		} catch (IOException e) {
			throw new BusinessException(DOOR_DUMMY_READ_FAILED + path);
		}
	}

	private DoorState toDoorStateMsg(String json) {
		try {
			DoorStateMsg doorStateMsg = ObjectMapperUtils.MAPPER.readValue(json, DoorStateMsg.class);
			return DoorStateMsg.Mapstruct.INSTANCE.toEntity(doorStateMsg);
		} catch (IOException e) {
			throw new BusinessException(DOOR_DUMMY_CONVERT_FAILED + json);
		}
	}
}
