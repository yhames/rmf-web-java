package com.rmf.apiserverjava.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmf.apiserverjava.global.utils.ObjectMapperUtils;
import com.rmf.apiserverjava.rmfapi.fleet.FleetLogApi;
import com.rmf.apiserverjava.rmfapi.fleet.FleetStateApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskEventLogApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskStateApi;
import com.rmf.apiserverjava.service.FleetAdapterFleetService;
import com.rmf.apiserverjava.service.FleetAdapterTaskService;

import lombok.extern.slf4j.Slf4j;

/**
 * FleetAdapterHandler.
 *
 * <p>
 *	FleetAdapterHandler 에서 WebSocket으로 전달하는 메시지를 처리하는 핸들러.
 * </p>
 */
@Slf4j
@Component
public class FleetAdapterHandler extends TextWebSocketHandler {

	private static final String INVALID_MSG = "Ignoring message, 'type' must include in msg field";
	private static final String READ_FAIL = "Error while read FleetAdapter message: ";
	private static final String UNKNOWN_TYPE = "Ignoring message, unknown type: {}";
	private static final String RECEIVE_MSG = "Received message: {}";

	private static final String TASK_STATE_UPDATE = "task_state_update";
	private static final String TASK_LOG_UPDATE = "task_log_update";
	private static final String FLEET_STATE_UPDATE = "fleet_state_update";
	private static final String FLEET_LOG_UPDATE = "fleet_log_update";

	private ObjectMapper objectMapper;
	private final FleetAdapterFleetService fleetAdapterFleetService;

	private final FleetAdapterTaskService fleetAdapterTaskService;

	public FleetAdapterHandler(FleetAdapterFleetService fleetAdapterFleetService,
		FleetAdapterTaskService fleetAdapterTaskService) {
		super();
		this.objectMapper = ObjectMapperUtils.MAPPER;
		this.fleetAdapterFleetService = fleetAdapterFleetService;
		this.fleetAdapterTaskService = fleetAdapterTaskService;
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		try {
			JsonNode data = objectMapper.readTree(message.getPayload());

			String type = data.path("type").asText(null);

			if (type != null) {
				processMessage(data, type);
			} else {
				log.warn(INVALID_MSG);
			}

		} catch (Exception e) {
			log.error(READ_FAIL, e);
		}
	}

	private void processMessage(JsonNode data, String type) throws JsonProcessingException {
		log.info(RECEIVE_MSG, data);
		JsonNode dataNode = data.path("data");
		if (type.equals(TASK_STATE_UPDATE)) {
			TaskStateApi taskStateApi = objectMapper.treeToValue(dataNode, TaskStateApi.class);
			fleetAdapterTaskService.saveOrUpdateTaskState(taskStateApi);
		} else if (type.equals(TASK_LOG_UPDATE)) {
			TaskEventLogApi taskEventLogApi = objectMapper.treeToValue(dataNode, TaskEventLogApi.class);
			fleetAdapterTaskService.saveTaskEventLog(taskEventLogApi);
		} else if (type.equals(FLEET_STATE_UPDATE)) {
			FleetStateApi fleetStateApi = objectMapper.treeToValue(dataNode, FleetStateApi.class);
			fleetAdapterFleetService.saveOrUpdateFleetState(fleetStateApi);
		} else if (type.equals(FLEET_LOG_UPDATE)) {
			FleetLogApi fleetLogApi = objectMapper.treeToValue(dataNode, FleetLogApi.class);
			fleetAdapterFleetService.saveFleetLog(fleetLogApi);
		} else {
			log.warn(UNKNOWN_TYPE, type);
		}
	}
}
