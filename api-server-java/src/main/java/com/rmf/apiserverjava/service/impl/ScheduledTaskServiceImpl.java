package com.rmf.apiserverjava.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rmf.apiserverjava.dto.scheduledtasks.CreateScheduledTaskDto;
import com.rmf.apiserverjava.dto.scheduledtasks.ScheduledTaskScheduleRequestDto;
import com.rmf.apiserverjava.dto.scheduledtasks.ScheduledTaskSearchCondition;
import com.rmf.apiserverjava.dto.scheduledtasks.UpdateScheduledTaskDto;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTask;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTaskSchedule;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.exception.custom.BusinessException;
import com.rmf.apiserverjava.global.exception.custom.InvalidClientArgumentException;
import com.rmf.apiserverjava.global.exception.custom.NotFoundException;
import com.rmf.apiserverjava.repository.ScheduledTaskRepository;
import com.rmf.apiserverjava.repository.ScheduledTaskScheduleRepository;
import com.rmf.apiserverjava.rmfapi.tasks.TaskRequestApi;
import com.rmf.apiserverjava.service.QuartzService;
import com.rmf.apiserverjava.service.ScheduledTaskService;
import com.rmf.apiserverjava.service.TaskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ScheduledTaskServiceImpl
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledTaskServiceImpl implements ScheduledTaskService {

	private static final String SCHEDULED_TASK_POST_FAILED = "Failed to save ScheduledTask: ";

	private static final String SCHEDULED_TASK_DELETE_FAILED = "Failed to delete ScheduledTask: ";

	private static final String SCHEDULED_TASK_CLEAR_FAILED = "Failed to clear ScheduledTask Event: ";

	private static final String SCHEDULED_TASK_DISPATCH_FAILED = "Failed to dispatch ScheduledTask Event: ";

	public static final String SCHEDULED_TASK_NOT_FOUND = "ScheduledTask not found";

	public static final String SCHEDULED_TASK_UPDATE_FAILED = "Failed to update ScheduledTask: ";

	public static final String SCHEDULED_TASK_EXCEPTION_INFO = "ScheduledTask {} is excepted on {}";

	private final ScheduledTaskRepository scheduledTaskRepository;

	private final ScheduledTaskScheduleRepository scheduledTaskScheduleRepository;

	private final QuartzService quartzService;

	private final TaskService taskService;

	/**
	 * getScheduledTasks
	 *
	 * <p>
	 *     ScheduledTask의 시작 시간이 startBefore보다 이전이고 종료 시간이 untilAfter보다 이후인 ScheduledTask를 검색합니다.
	 * </p>
	 */
	@Override
	public List<ScheduledTask> getScheduledTasks(ScheduledTaskSearchCondition condition) {
		return scheduledTaskRepository.searchStartBeforeUntilAfter(condition);
	}

	/**
	 * postScheduledTask
	 *
	 * <p>
	 *     ScheduledTask를 저장하고 스케줄러에 등록합니다.
	 *     ScheduledTask를 저장할 때 ScheduledTaskSchedule도 함께 저장합니다.
	 * </p>
	 */
	@Override
	@Transactional
	public Optional<ScheduledTask> postScheduledTask(CreateScheduledTaskDto createScheduledTaskDto, User user) {
		ScheduledTask newScheduledTask = CreateScheduledTaskDto.MapStruct.INSTANCE
			.toEntity(createScheduledTaskDto, user);
		for (ScheduledTaskSchedule schedule : newScheduledTask.getSchedules()) {
			schedule.setScheduledTaskWith(newScheduledTask);
		}
		try {
			ScheduledTask savedScheduledTask = scheduledTaskRepository.save(newScheduledTask);
			scheduledTaskScheduleRepository.saveAll(savedScheduledTask.getSchedules());
			quartzService.scheduleTask(savedScheduledTask);
			return Optional.of(savedScheduledTask);
		} catch (Exception e) {
			throw new InvalidClientArgumentException(SCHEDULED_TASK_POST_FAILED + e.getMessage());
		}
	}

	/**
	 * getScheduledTask
	 *
	 * <p>
	 *     ScheduledTaskId를 기준으로 ScheduledTask를 조회합니다.
	 * </p>
	 */
	@Override
	public Optional<ScheduledTask> getScheduledTask(int scheduledTaskId) {
		return scheduledTaskRepository.findById(scheduledTaskId);
	}

	/**
	 * deleteScheduledTask
	 *
	 * <p>
	 *     ScheduledTaskId를 기준으로 ScheduledTask를 삭제합니다.
	 * </p>
	 */
	@Override
	@Transactional
	public void deleteScheduledTask(int scheduledTaskId) {
		ScheduledTask scheduledTask = scheduledTaskRepository.findById(scheduledTaskId)
			.orElseThrow(() -> new NotFoundException(SCHEDULED_TASK_NOT_FOUND));
		try {
			quartzService.unScheduleTask(scheduledTask);
			scheduledTaskRepository.delete(scheduledTask);
		} catch (Exception e) {
			throw new BusinessException(SCHEDULED_TASK_DELETE_FAILED + e.getMessage());
		}
	}

	/**
	 * updateScheduledTask
	 *
	 * <p>
	 *     ScheduledTaskId를 기준으로 ScheduledTask를 수정합니다.
	 * </p>
	 */
	@Override
	@Transactional
	public ScheduledTask updateScheduledTask(int scheduledTaskId,
		UpdateScheduledTaskDto updateScheduledTaskDto) {
		ScheduledTask scheduledTask = scheduledTaskRepository.findById(scheduledTaskId)
			.orElseThrow(() -> new NotFoundException(SCHEDULED_TASK_NOT_FOUND));
		TaskRequestApi newTaskRequest = updateScheduledTaskDto.getTaskRequest();
		List<ScheduledTaskSchedule> newSchedules = updateScheduledTaskDto.getSchedules().stream()
			.map(ScheduledTaskScheduleRequestDto.MapStruct.INSTANCE::toEntity)
			.peek(schedule -> schedule.setScheduledTaskWith(scheduledTask))
			.toList();
		try {
			scheduledTaskScheduleRepository.deleteAllByScheduledTaskId(scheduledTask.getId());
			scheduledTask.resetExceptDates();
			scheduledTask.update(newTaskRequest, newSchedules);
			scheduledTaskScheduleRepository.saveAll(newSchedules);
			quartzService.scheduleTask(scheduledTask);
			return scheduledTask;
		} catch (InvalidClientArgumentException e) {
			throw e;
		} catch (Exception e) {
			throw new BusinessException(SCHEDULED_TASK_UPDATE_FAILED + e.getMessage());
		}
	}

	/**
	 * updateScheduledTaskSingleEvent
	 *
	 * <p>
	 *     exceptDate에 해당하는 날짜를 exceptDates에 추가하고,
	 *     해당 날짜의 스케쥴러를 새로 등록합니다.
	 * </p>
	 */
	@Override
	@Transactional
	public ScheduledTask updateScheduledTaskSingleEvent(int scheduledTaskId, String exceptDate,
		UpdateScheduledTaskDto updateScheduledTaskDto, User user) {
		ScheduledTask scheduledTask = scheduledTaskRepository.findById(scheduledTaskId)
			.orElseThrow(() -> new NotFoundException(SCHEDULED_TASK_NOT_FOUND));
		ScheduledTask newScheduledTask = UpdateScheduledTaskDto.MapStruct.INSTANCE
			.toEntity(updateScheduledTaskDto, user);
		for (ScheduledTaskSchedule schedule : newScheduledTask.getSchedules()) {
			schedule.setScheduledTaskWith(newScheduledTask);
		}
		try {
			scheduledTask.addExceptDates(exceptDate);
			ScheduledTask savedScheduledTask = scheduledTaskRepository.save(newScheduledTask);
			scheduledTaskScheduleRepository.saveAll(savedScheduledTask.getSchedules());
			quartzService.scheduleTask(savedScheduledTask);
			return savedScheduledTask;
		} catch (InvalidClientArgumentException e) {
			throw e;
		} catch (Exception e) {
			throw new BusinessException(SCHEDULED_TASK_UPDATE_FAILED + e.getMessage());
		}
	}

	/**
	 * clearScheduledTaskEvent
	 *
	 * <p>
	 *     ScheduledTaskId에 해당하는 ScheduledTask의 스케쥴링을 삭제하고,
	 *     exceptDate에 해당하는 날짜를 exceptDates에 추가하여 스케쥴링을 다시 등록합니다.
	 * </p>
	 */
	@Override
	@Transactional
	public void clearScheduledTaskEvent(int scheduledTaskId, String eventDate) {
		ScheduledTask scheduledTask = scheduledTaskRepository.findById(scheduledTaskId)
			.orElseThrow(() -> new NotFoundException(SCHEDULED_TASK_NOT_FOUND));
		try {
			quartzService.unScheduleTask(scheduledTask);
			scheduledTask.addExceptDates(eventDate);
			quartzService.scheduleTask(scheduledTask);
		} catch (Exception e) {
			throw new BusinessException(SCHEDULED_TASK_CLEAR_FAILED + e.getMessage());
		}
	}

	/**
	 * dispatchScheduledTask
	 *
	 * <p>
	 *     TaskService의 postDispatchTask를 호출하여 ScheduledTask를 실행합니다.
	 *     ScheduledTask의 마지막 실행 시간을 업데이트합니다.
	 *     예외 날짜에 포함되어 있으면 실행하지 않습니다. (exceptDates는 ISO 8601 형식의 날짜 문자열)
	 * </p>
	 */
	@Override
	@Transactional
	public void dispatchScheduledTask(int scheduledTaskId) {
		ScheduledTask scheduledTask = scheduledTaskRepository.findById(scheduledTaskId)
			.orElseThrow(() -> new NotFoundException(SCHEDULED_TASK_NOT_FOUND));
		LocalDateTime now = LocalDateTime.now();
		List<String> exceptDates = scheduledTask.getExceptDates();
		if (exceptDates != null && exceptDates.contains(now.format(DateTimeFormatter.ISO_DATE))) {
			log.info(SCHEDULED_TASK_EXCEPTION_INFO, scheduledTaskId, now);
			return;
		}
		try {
			taskService.postDispatchTask(scheduledTask.getTaskRequest());
			scheduledTask.updateLastRun(now);
		} catch (Exception e) {
			throw new BusinessException(SCHEDULED_TASK_DISPATCH_FAILED + e.getMessage());
		}
	}
}
