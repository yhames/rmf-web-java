package com.rmf.apiserverjava.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rmf.apiserverjava.dto.alerts.AlertResponseDto;
import com.rmf.apiserverjava.dto.alerts.CreateAlertDto;
import com.rmf.apiserverjava.entity.alerts.Alert;
import com.rmf.apiserverjava.rmfapi.Tier;
import com.rmf.apiserverjava.rmfapi.fleet.LogEntryApi;
import com.rmf.apiserverjava.rmfapi.tasks.PhasesApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskEventLogApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskStateApi;
import com.rmf.apiserverjava.rxjava.eventbus.AlertEvents;
import com.rmf.apiserverjava.rxjava.eventbus.TaskEvents;
import com.rmf.apiserverjava.service.AlertService;
import com.rmf.apiserverjava.service.FleetAdapterTaskService;
import com.rmf.apiserverjava.service.TaskService;

import lombok.RequiredArgsConstructor;

/**
 * FleetAdapterTaskService의 구현체.
 * */

@Service
@RequiredArgsConstructor
public class FleetAdapterTaskServiceImpl implements FleetAdapterTaskService {

	private final TaskService taskService;
	private final TaskEvents taskEvents;
	private final AlertEvents alertEvents;
	private final AlertService alertService;

	@Override
	public void saveOrUpdateTaskState(TaskStateApi taskStateApi) {
		taskService.updateOrCreateTaskState(taskStateApi);
		taskEvents.getTaskStatesEvent().onNext(taskStateApi);
		if (taskStateApi.getStatus().equals(TaskStateApi.Status.completed)) {
			CreateAlertDto createAlertDto = new CreateAlertDto(taskStateApi.getBooking().getId(), Alert.Category.TASK);
			Optional<Alert> alert = alertService.createAlert(createAlertDto);
			if (alert.isPresent()) {
				alertEvents.getAlertsEvent().onNext(AlertResponseDto.MapStruct.INSTANCE.toDto(alert.get()));
			}
		}
	}

	@Override
	public void saveTaskEventLog(TaskEventLogApi taskEventLogApi) {
		taskService.saveTaskLog(taskEventLogApi);
		taskEvents.getTaskEventLogsEvent().onNext(taskEventLogApi);
		if (taskLogHasError(taskEventLogApi)) {
			CreateAlertDto createAlertDto = new CreateAlertDto(taskEventLogApi.getTaskId(), Alert.Category.TASK);
			Optional<Alert> alert = alertService.createAlert(createAlertDto);
			if (alert.isPresent()) {
				alertEvents.getAlertsEvent().onNext(AlertResponseDto.MapStruct.INSTANCE.toDto(alert.get()));
			}
		}
	}

	private boolean taskLogHasError(TaskEventLogApi taskEventLogApi) {
		if (taskEventLogApi.getLog() != null) {
			for (LogEntryApi log : taskEventLogApi.getLog()) {
				if (log.getTier().equals(Tier.error)) {
					return true;
				}
			}
		}
		if (taskEventLogApi.getPhases() != null) {
			for (Map.Entry<String, PhasesApi> phase : taskEventLogApi.getPhases().entrySet()) {
				if (logPhaseHasError(phase.getValue())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean logPhaseHasError(PhasesApi phasesApi) {
		if (phasesApi.getLog() != null) {
			for (LogEntryApi log : phasesApi.getLog()) {
				if (log.getTier().equals(Tier.error)) {
					return true;
				}
			}
		}
		if (phasesApi.getEvents() != null) {
			for (Map.Entry<String, List<LogEntryApi>> eventLogs : phasesApi.getEvents().entrySet()) {
				for (LogEntryApi eventLog : eventLogs.getValue()) {
					if (eventLog.getTier().equals(Tier.error)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
