package com.rmf.apiserverjava.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rmf.apiserverjava.dto.page.PaginationDto;
import com.rmf.apiserverjava.dto.tasks.TaskLogRequestDto;
import com.rmf.apiserverjava.dto.tasks.TaskStatesQueryDto;
import com.rmf.apiserverjava.dto.tasks.TaskStatesQueryRequestDto;
import com.rmf.apiserverjava.entity.tasks.TaskEventLog;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogLog;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhases;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhasesEvents;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhasesEventsLog;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhasesLog;
import com.rmf.apiserverjava.entity.tasks.TaskRequest;
import com.rmf.apiserverjava.entity.tasks.TaskState;
import com.rmf.apiserverjava.global.parser.LogBetweenParser;
import com.rmf.apiserverjava.repository.TaskEventLogLogRepository;
import com.rmf.apiserverjava.repository.TaskEventLogPhasesEventsLogRepository;
import com.rmf.apiserverjava.repository.TaskEventLogPhasesEventsRepository;
import com.rmf.apiserverjava.repository.TaskEventLogPhasesLogRepository;
import com.rmf.apiserverjava.repository.TaskEventLogPhasesRepository;
import com.rmf.apiserverjava.repository.TaskEventLogRepository;
import com.rmf.apiserverjava.repository.TaskRequestRepository;
import com.rmf.apiserverjava.repository.TaskStateQueryRepository;
import com.rmf.apiserverjava.repository.TaskStateRepository;
import com.rmf.apiserverjava.rmfapi.Tier;
import com.rmf.apiserverjava.rmfapi.fleet.LogEntryApi;
import com.rmf.apiserverjava.rmfapi.tasks.PhasesApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskEventLogApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskRequestApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskStateApi;
import com.rmf.apiserverjava.rxjava.eventbus.TaskEvents;
import com.rmf.apiserverjava.service.TaskService;

import lombok.RequiredArgsConstructor;

/**
 * TaskService 인터페이스를 구현한다.
 * */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
	public static final String ACK_TASK = "Task completion acknowledged by %s";
	public static final String NEW_PHASE_SEQ = "0";
	public static final String TASK_COMPLETE_LOG_MSG = "Task complete";

	private final TaskRequestRepository taskRequestRepository;
	private final TaskStateRepository taskStateRepository;
	private final TaskEventLogRepository logRepository;
	private final TaskEventLogLogRepository logLogRepository;
	private final TaskEventLogPhasesRepository logPhasesRepository;
	private final TaskEventLogPhasesLogRepository logPhasesLogRepository;
	private final TaskEventLogPhasesEventsRepository logPhasesEventsRepository;
	private final TaskEventLogPhasesEventsLogRepository logPhasesEventsLogRepository;
	private final TaskStateQueryRepository taskStateQueryRepository;
	private final LogBetweenParser logBetweenParser;
	private final TaskEvents taskEvents;

	/*
	 * TaskId를 기준으로 TaskRequest를 조회, TaskRequestApi로 변환하여 반환한다.
	 * */
	@Override
	public Optional<TaskRequestApi> getTaskRequest(String taskId) {
		return taskRequestRepository.findById(taskId).map(TaskRequest::getRequest);
	}

	/*
	 * TaskStateQueryRequestDto를 기준으로 TaskStatesQueryDto를 생성하여
	 * TaskState의 목록을 조회, TaskStateApi로 변환하여 반환한다.
	 * */
	@Override
	public List<TaskStateApi> getTaskStateList(TaskStatesQueryRequestDto taskStatesQueryRequestDto) {
		TaskStatesQueryDto.TaskStatesQueryDtoBuilder builder = TaskStatesQueryDto.builder();
		if (taskStatesQueryRequestDto.getTaskId() != null) {
			builder.id(List.of(taskStatesQueryRequestDto.getTaskId().split(",")));
		}
		if (taskStatesQueryRequestDto.getCategory() != null) {
			builder.category(List.of(taskStatesQueryRequestDto.getCategory().split(",")));
		}
		if (taskStatesQueryRequestDto.getAssignedTo() != null) {
			builder.assignedTo(List.of(taskStatesQueryRequestDto.getAssignedTo().split(",")));
		}
		if (taskStatesQueryRequestDto.getStartTimeBetween() != null) {
			builder.startTimeBetween(logBetweenParser.parseBetween(taskStatesQueryRequestDto.getStartTimeBetween()));
		}
		if (taskStatesQueryRequestDto.getFinishTimeBetween() != null) {
			builder.finishTimeBetween(logBetweenParser.parseBetween(taskStatesQueryRequestDto.getFinishTimeBetween()));
		}
		if (taskStatesQueryRequestDto.getStatus() != null) {
			builder.status(List.of(taskStatesQueryRequestDto.getStatus().split(",")));
		}
		TaskStatesQueryDto statesQueryDto = builder.build();
		PaginationDto paginationDto = PaginationDto.builder()
			.limit(taskStatesQueryRequestDto.getLimit())
			.offset(taskStatesQueryRequestDto.getOffset())
			.orderBy(taskStatesQueryRequestDto.getOrderBy())
			.build();
		return taskStateQueryRepository.findAllTaskStateByQuery(statesQueryDto, paginationDto)
			.stream().map(TaskState::getData).toList();
	}

	/*
	 * TaskId를 기준으로 TaskState를 조회, TaskStateApi로 변환하여 반환한다.
	 * */
	@Override
	public Optional<TaskStateApi> getTaskState(String taskId) {
		return taskStateRepository.findById(taskId).map(TaskState::getData);
	}

	/*
	 * task_id에 해당하는 데이터가 없으면 Optional.empty()를 반환한다.
	 * 존재할 경우 TaskEventLogApi 객체를 반환한다.
	 * */
	@Override
	public Optional<TaskEventLogApi> getTaskLog(TaskLogRequestDto taskLogRequestDto) {
		String taskId = taskLogRequestDto.getTaskId();
		long start = taskLogRequestDto.getTimeRange().getStartTimeMillis();
		long end = taskLogRequestDto.getTimeRange().getEndTimeMillis();

		Optional<TaskEventLog> taskEventLogOptional = logRepository.findByIdFetchPhases(taskId);
		if (!taskEventLogOptional.isPresent()) {
			return Optional.empty();
		}
		TaskEventLog taskEventLog = taskEventLogOptional.get();
		TaskEventLogApi logApi = getTaskEventLogApi(taskEventLog, start, end);
		return Optional.of(logApi);
	}

	/*
	 * TaskEventLogApi를 생성하여 반환한다.
	 * TaskEventLogApi는 TaskEventLog, TaskEventLogLog, PhaseApi를 포함한다.
	 * */
	private TaskEventLogApi getTaskEventLogApi(TaskEventLog taskEventLog, long start, long end) {
		TaskEventLogApi.TaskEventLogApiBuilder logApiBuilder = TaskEventLogApi.builder();
		logApiBuilder.taskEventLog(taskEventLog);
		logApiBuilder.log(
			logLogRepository.findTaskEventLogLogByIdAndTimeRangeFetch(taskEventLog.getTaskId(), start, end));
		Map<String, PhasesApi> phasesApiMap = new HashMap<>();
		List<TaskEventLogPhases> Phases = logPhasesRepository.findByIdFetchEvents(taskEventLog.getTaskId());
		for (TaskEventLogPhases phase : Phases) {
			phasesApiMap.put(phase.getPhase(), getPhasesApi(phase, start, end));
		}
		logApiBuilder.phases(phasesApiMap);
		TaskEventLogApi logApi = logApiBuilder.build();
		return logApi;
	}

	/*
	* taskEventLogApi의 PhasesApi를 생성하여 반환한다.
	* PhasesApi는 TaskEventLogPhases, TaskEventLogPhasesLog,
	* TaskEventLogPhasesEvents, TaskEventLogPhasesEventsLog를 포함한다.
	* */
	private PhasesApi getPhasesApi(TaskEventLogPhases phase, long start, long end) {
		PhasesApi.PhasesApiBuilder phasesApiBuilder = PhasesApi.builder();
		List<TaskEventLogPhasesLog> phasesLogs
			= logPhasesLogRepository.findLogPhasesLogByLogPhasesIdAndTimeRangeFetch(phase.getId(), start,
			end);
		List<TaskEventLogPhasesEvents> phasesEvents
			= logPhasesEventsRepository.findLogPhasesEventsLogByLogPhasesIdAndTimeRangeFetch(
			phase.getId(), start, end);
		phasesApiBuilder.phases(phase);
		phasesApiBuilder.log(phasesLogs);
		phasesApiBuilder.events(phasesEvents);
		PhasesApi phasesApi = phasesApiBuilder.build();
		return phasesApi;
	}

	/*
	 * TaskStateApi를 받아 TaskState를 저장 혹은 업데이트한다.
	 * */
	@Override
	@Transactional
	public void updateOrCreateTaskState(TaskStateApi taskStateApi) {

		Optional<TaskState> taskStateOptional = taskStateRepository.findById(taskStateApi.getBooking().getId());
		if (taskStateOptional.isPresent()) {
			TaskState existing = taskStateOptional.get();
			existing.updateTaskState(taskStateApi);
		} else {
			TaskState taskState = TaskState.builder().id(taskStateApi.getBooking().getId())
				.data(taskStateApi)
				.category(taskStateApi.getCategory().getCategory())
				.assignedTo(taskStateApi.getAssignedTo().getName())
				.unixMillisStartTime(taskStateApi.getUnixMillisStartTime())
				.unixMillisFinishTime(taskStateApi.getUnixMillisFinishTime())
				.status(taskStateApi.getStatus().getValue())
				.unixMillisRequestTime(taskStateApi.getBooking().getUnixMillisRequestTime())
				.requester(taskStateApi.getBooking().getRequester())
				.build();
			taskStateRepository.save(taskState);
		}
	}

	/*
	 * TaskEventLogApi를 받아 TaskEventLog를 저장한다.
	 * 연관된 모든 로그 데이터도 저장한다.
	 * */
	@Override
	@Transactional
	public void saveTaskLog(TaskEventLogApi taskEventLogApi) {
		TaskEventLog dbTaskLog = getOrCreateTaskLog(taskEventLogApi.getTaskId());
		List<LogEntryApi> logs = taskEventLogApi.getLog();
		if (logs != null) {
			saveTaskLogLogs(dbTaskLog, logs);
		}
		Map<String, PhasesApi> phases = taskEventLogApi.getPhases();
		if (phases != null) {
			savePhases(dbTaskLog, phases);
		}
	}

	/*
	 * taskId로 taskEventLog를 조회하고, 없을 경우 새로 생성한다.
	 * */
	private TaskEventLog getOrCreateTaskLog(String taskId) {
		Optional<TaskEventLog> logOptional = logRepository.findById(taskId);
		if (logOptional.isPresent()) {
			return logOptional.get();
		}
		TaskEventLog taskEventLog = TaskEventLog.builder().taskId(taskId).build();
		return logRepository.save(taskEventLog);
	}

	/*
	 * taskEventLog의 로그 데이터를 저장한다.
	 * */
	private void saveTaskLogLogs(TaskEventLog dbTaskLog, List<LogEntryApi> logs) {
		List<TaskEventLogLog> taskLogLogs = logs.stream()
			.map(LogEntryApi.MapStruct.INSTANCE::toTaskEventLogLogEntity)
			.map((log) -> log.setTaskEventLogWithoutRelation(dbTaskLog))
			.collect(Collectors.toList());
		logLogRepository.saveAll(taskLogLogs);
	}

	/*
	 * taskEventLog의 phases 데이터를 저장한다
	 * 연관된 모든 로그 데이터도 저장한다.
	 * */
	private void savePhases(TaskEventLog dbTaskLog, Map<String, PhasesApi> phases) {
		for (Map.Entry<String, PhasesApi> entry : phases.entrySet()) {
			String phaseId = entry.getKey();
			PhasesApi phasesApi = entry.getValue();
			TaskEventLogPhases dbPhase = getOrCreatePhases(dbTaskLog, phaseId);
			List<LogEntryApi> logs = phasesApi.getLog();
			if (logs != null) {
				savePhaseLogs(logs, dbPhase);
			}
			Map<String, List<LogEntryApi>> events = phasesApi.getEvents();
			if (events != null) {
				savePhaseEvents(events, dbPhase);
			}
		}
	}

	/*
	 * phaseId로 taskEventLogPhases를 조회하고, 없을 경우 새로 생성한다.
	 * */
	private TaskEventLogPhases getOrCreatePhases(TaskEventLog dbTaskLog, String phaseId) {
		List<TaskEventLogPhases> phasesList = logPhasesRepository.findAllByTaskAndPhase(dbTaskLog.getTaskId(), phaseId);
		if (phasesList.size() > 0) {
			return phasesList.get(0);
		}
		TaskEventLogPhases newPhases = TaskEventLogPhases.builder().task(dbTaskLog).phase(phaseId).build();
		;
		return logPhasesRepository.save(newPhases);
	}

	/*
	 * phase의 로그 데이터를 저장한다.
	 * */
	private void savePhaseLogs(List<LogEntryApi> logs, TaskEventLogPhases dbPhase) {
		List<TaskEventLogPhasesLog> phaseLogs = logs.stream()
			.map(LogEntryApi.MapStruct.INSTANCE::toTaskEventLogPhasesLogEntity)
			.map((log) -> log.setTaskEventLogPhasesWithoutRelation(dbPhase))
			.collect(Collectors.toList());
		logPhasesLogRepository.saveAll(phaseLogs);
	}

	/*
	 * taskEventLog의 phase의 events 데이터를 저장한다.
	 * */
	private void savePhaseEvents(Map<String, List<LogEntryApi>> events, TaskEventLogPhases dbPhase) {
		for (Map.Entry<String, List<LogEntryApi>> entry : events.entrySet()) {
			String eventId = entry.getKey();
			List<LogEntryApi> logs = entry.getValue();
			TaskEventLogPhasesEvents dbEvent = getOrCreatePhasesEvents(dbPhase, eventId);
			savePhaseEventsLogs(logs, dbEvent);
		}
	}

	/*
	 * TaskEventLogPhasesEvents를 조회하고, 없을 경우 새로 생성한다.
	 * */
	private TaskEventLogPhasesEvents getOrCreatePhasesEvents(TaskEventLogPhases dbPhases, String eventId) {
		List<TaskEventLogPhasesEvents> eventList = logPhasesEventsRepository.findAllByPhasesAndEvent(dbPhases.getId(),
			eventId);
		if (!eventList.isEmpty()) {
			return eventList.get(0);
		}
		TaskEventLogPhasesEvents newEvent = TaskEventLogPhasesEvents.builder().phase(dbPhases).event(eventId).build();
		return logPhasesEventsRepository.save(newEvent);
	}

	/*
	 * TaskEventLogPhasesEvents의 로그데이터를 저장한다.
	 * */
	private void savePhaseEventsLogs(List<LogEntryApi> logs, TaskEventLogPhasesEvents dbEvent) {
		List<TaskEventLogPhasesEventsLog> eventLogs = logs.stream()
			.map(LogEntryApi.MapStruct.INSTANCE::toTaskEventLogPhasesEventLogEntity)
			.map((log) -> log.setTaskEventLogPhasesEventsWithoutRelation(dbEvent))
			.collect(Collectors.toList());
		logPhasesEventsLogRepository.saveAll(eventLogs);
	}

	@Override
	public void postDispatchTask(TaskRequestApi taskRequest) {
		// TODO: Implement this method
	}

	/**
	 * Alerts ack 이벤트 발생시 관련 TaskState, TaskEventLog의 phase를 추가한다.
	 */
	@Override
	@Transactional
	public void saveLogAcknowledgedTaskCompletion(String taskId, String acknowledgedBy, long ackTimeMills) {
		Optional<TaskEventLog> taskLog = logRepository.findByIdFetchPhases(taskId);
		Optional<TaskState> taskState = taskStateRepository.findById(taskId);

		String nextPhaseId;
		if (taskLog.isPresent()) {
			nextPhaseId = String.valueOf(taskLog.get().getPhases().size() + 1);
		} else if (taskState.isPresent()) {
			nextPhaseId = String.valueOf(taskState.get().getData().getPhases().size() + 1);
		} else {
			return;
		}

		if (taskLog.isPresent()) {
			TaskEventLogPhases newPhase = createLogPhasesWithoutRelationMapping(taskLog.get(), nextPhaseId);
			TaskEventLogPhasesEvents newPhaseEvent = createLogPhasesEventsWithoutRelationMapping(newPhase);
			createLogPhasesEventsLogWithoutRelationMapping(newPhaseEvent, ackTimeMills, acknowledgedBy);
		}

		if (taskState.isPresent()) {
			insertNewPhaseInTaskState(taskState.get(), nextPhaseId);
			taskEvents.getTaskStatesEvent().onNext(taskState.get().getData());
		}
	}

	/**
	 * 연관관계 동기화를 하지 않고 TaskEventLogPhases를 생성한다.
	 */
	private TaskEventLogPhases createLogPhasesWithoutRelationMapping(TaskEventLog taskLog, String phase) {
		TaskEventLogPhases newPhase = TaskEventLogPhases.builder()
			.task(taskLog)
			.phase(phase)
			.build();
		return logPhasesRepository.save(newPhase);
	}

	/**
	 * 연관관계 동기화를 하지 않고 TaskEventLogPhasesEvents를 생성한다.
	 */
	private TaskEventLogPhasesEvents createLogPhasesEventsWithoutRelationMapping(TaskEventLogPhases logPhases) {
		TaskEventLogPhasesEvents newPhaseEvent = TaskEventLogPhasesEvents.builder()
			.phase(logPhases)
			.event(NEW_PHASE_SEQ)
			.build();
		return logPhasesEventsRepository.save(newPhaseEvent);
	}

	/**
	 * 연관관계 동기화를 하지 않고 TaskEventLogPhasesEventsLog를 생성한다.
	 */
	private TaskEventLogPhasesEventsLog createLogPhasesEventsLogWithoutRelationMapping(
		TaskEventLogPhasesEvents logPhasesEvents, long ackTimeMills, String acknowledgedBy) {
		TaskEventLogPhasesEventsLog newPhaseEventLog = TaskEventLogPhasesEventsLog.builder()
			.seq(Integer.parseInt(NEW_PHASE_SEQ))
			.unixMillisTime(ackTimeMills)
			.tier(Tier.warning)
			.text(String.format(ACK_TASK, acknowledgedBy))
			.event(logPhasesEvents)
			.build();
		return logPhasesEventsLogRepository.save(newPhaseEventLog);
	}

	/**
	 * TaskState에 새로운 Phase를 추가한다.
	 */
	@Transactional
	public void insertNewPhaseInTaskState(TaskState taskState, String phaseId) {
		TaskStateApi.Phase newTaskStatePhase = TaskStateApi.Phase.builder()
			.id(new TaskStateApi.Id(Long.valueOf(phaseId)))
			.category(new TaskStateApi.Category(TASK_COMPLETE_LOG_MSG))
			.build();
		taskState.getData().getPhases().put(phaseId, newTaskStatePhase);
	}
}
