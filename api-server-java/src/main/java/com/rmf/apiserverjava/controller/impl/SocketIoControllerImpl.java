package com.rmf.apiserverjava.controller.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.rmf.apiserverjava.controller.SocketIoController;
import com.rmf.apiserverjava.entity.buildingmaps.BuildingMap;
import com.rmf.apiserverjava.global.annotation.socketio.ListenEvent;
import com.rmf.apiserverjava.global.annotation.socketio.SendEvent;
import com.rmf.apiserverjava.rxjava.eventconsumer.AlertConsumers;
import com.rmf.apiserverjava.rxjava.eventconsumer.BeaconConsumers;
import com.rmf.apiserverjava.rxjava.eventconsumer.FleetConsumers;
import com.rmf.apiserverjava.rxjava.eventconsumer.RmfConsumers;
import com.rmf.apiserverjava.rxjava.eventconsumer.TaskConsumers;
import com.rmf.apiserverjava.service.SocketIoSessionService;
import com.rmf.apiserverjava.service.impl.SocketIoSessionServiceImpl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SocketIoControllerImpl.
 *
 * <p>
 *	 SocketIoController의 구현체
 * </p>
 */
@Component
@Slf4j
public class SocketIoControllerImpl implements SocketIoController {
	private static final String SUBSCRIBE_EVENT = "subscribe";
	private static final String UNSUBSCRIBE_EVENT = "unsubscribe";
	private static final String ALERTS_ROOM_EVENT = "/alerts";
	private static final String BEACONS_ROOM_EVENT = "/beacons";
	private static final String TASK_STATE_ROOM_EVENT = "/tasks/%s/state";

	private static final String TASK_LOG_ROOM_EVENT = "/tasks/%s/log";
	private static final String FLEET_STATE_ROOM_EVENT = "/fleets/%s/state";
	private static final String FLEET_LOG_ROOM_EVENT = "/fleets/%s/log";
	private static final String BUILDING_MAP_ROOM_EVENT = "/building_map";
	private static final String DOOR_STATE_ROOM_EVENT = "/doors/%s/state";
	private static final String DOOR_HEALTH_ROOM_EVENT = "/doors/%s/health";

	private final SocketIOServer server;
	private final AlertConsumers alertConsumers;
	private final BeaconConsumers beaconConsumers;
	private final TaskConsumers taskConsumers;
	private final RmfConsumers rmfConsumers;
	private final FleetConsumers fleetConsumers;
	private final SocketIoSessionService sessionService;

	/**
	 * 이벤트 컨슈머 의존성 설정 및 소켓 이벤트 리스너 등록
	 */
	public SocketIoControllerImpl(SocketIOServer server, AlertConsumers alertConsumers,
		BeaconConsumers beaconConsumers, TaskConsumers taskConsumers, RmfConsumers rmfConsumers,
		FleetConsumers fleetConsumers, SocketIoSessionService sessionService) {
		this.server = server;

		this.alertConsumers = alertConsumers;
		this.beaconConsumers = beaconConsumers;
		this.taskConsumers = taskConsumers;
		this.rmfConsumers = rmfConsumers;
		this.fleetConsumers = fleetConsumers;
		this.sessionService = sessionService;

		// 소켓 이벤트 리스너 등록
		server.addConnectListener(listenConnected());
		server.addDisconnectListener(listenDisconnected());
		server.addEventListener(SUBSCRIBE_EVENT, Message.class, listenSubscribe());
		server.addEventListener(UNSUBSCRIBE_EVENT, Message.class, listenUnsubscribe());

		// 전송할 소켓 이벤트 등록
		sendAlertsRoom();
		sendBeaconsRoom();
		sendTaskStatesRoomByTaskId();
		sendTasksLogsRoomByTaskId();
		sendFleetStatesRoomByName();
		sendFleetLogsRoomByName();
		sendBuildingMap();
		sendDoorState();
		sendDoorHealth();
	}

	/**
	 * 클라이언트 연결 리스너
	 */
	@ListenEvent(eventPath = "/connect")
	public ConnectListener listenConnected() {
		return (client) -> {
			if (sessionService.isNewConnection(client)) {
				Map<String, List<String>> params = client.getHandshakeData().getUrlParams();
				log.info("connect:" + params.toString());
				sessionService.manageClient(client);
			}
		};
	}

	/**
	 * 클라이언트 연결 해제 리스너
	 */
	@ListenEvent(eventPath = "/disconnect")
	public DisconnectListener listenDisconnected() {
		return client -> {
			String sessionId = client.getSessionId().toString();
			sessionService.stopManageClient(client);
			log.info("disconnect: " + sessionId);
			client.getAllRooms().clear();
			client.disconnect();
		};
	}

	/**
	 * 클라이언트 구독 리스너. netty에는 기본 제공되지 않아서 커스텀 이벤트로 구현
	 */
	@ListenEvent(eventPath = "/subscribe")
	public DataListener<Message> listenSubscribe() {
		return (senderClient, data, ackSender) -> {
			log.info("subscribe clients: " + senderClient.getSessionId() + " to room: " + data.getRoom());
			senderClient.joinRoom(data.getRoom());
			senderClient.sendEvent("subscribe", new Success(true));
			if (data.getRoom().equals(BUILDING_MAP_ROOM_EVENT)) {
				sendInitBuildingMap(senderClient);    // 초기 건물 맵 데이터 전송
			}
		};
	}

	/**
	 * sendInitBuildingMap
	 *
	 * <p>
	 *    클라이언트에게 초기 건물 맵 데이터 전송.
	 *    건물 맵 데이터가 없을 경우 아무런 데이터도 전송하지 않는다.
	 * </p>
	 */
	private void sendInitBuildingMap(SocketIOClient senderClient) {
		BuildingMap value = rmfConsumers.getRmfEvents().getBuildingMapEvent().getValue();    // 건물 맵 데이터가 없을 경우 null
		if (value == null) {
			return;
		}
		senderClient.sendEvent(BUILDING_MAP_ROOM_EVENT, value.getData());
	}

	/**
	 * 클라이언트 구독 해제 리스너. netty에는 기본 제공되지 않아서 커스텀 이벤트로 구현
	 */
	@ListenEvent(eventPath = "/unsubscribe")
	public DataListener<Message> listenUnsubscribe() {
		return (senderClient, data, ackSender) -> {
			log.info("unsubscribe clients: " + senderClient.getSessionId());
			senderClient.leaveRoom(data.getRoom());
			senderClient.sendEvent("unsubscribe", new Success(true));
		};
	}

	/**
	 * Alerts 룸에 존재하는 사용자에게 알림을 전송
	 */
	@SendEvent(eventPath = "/alerts")
	public void sendAlertsRoom() {
		alertConsumers.getAlertsConsumer().subscribe(data -> {
			log.info("Data send start: " + data);
			server.getRoomOperations(ALERTS_ROOM_EVENT).sendEvent(ALERTS_ROOM_EVENT, data);
		}, error -> log.error("Error in alertsStream: " + error.getMessage()));
	}

	/**
	 * Beacons 룸에 존재하는 사용자에게 알림을 전송
	 */
	@SendEvent(eventPath = "/beacons")
	public void sendBeaconsRoom() {
		beaconConsumers.getBeaconsConsumer().subscribe(data -> {
			log.info("Data send start: " + data);
			server.getRoomOperations(BEACONS_ROOM_EVENT).sendEvent(BEACONS_ROOM_EVENT, data);
		}, error -> log.error("Error in beaconsStream: " + error.getMessage()));
	}

	/**
	 * taskState의 Id와 일치하는 room에 존재하는 사용자에게 taskState 데이터 전송
	 */
	@SendEvent(eventPath = "/tasks/{taskId}/state")
	public void sendTaskStatesRoomByTaskId() {
		taskConsumers.getTaskStatesConsumer().subscribe(data -> {
			log.info("Task State Data send start: " + data);
			String roomName = String.format(TASK_STATE_ROOM_EVENT, data.getBooking().getId());
			server.getRoomOperations(roomName).sendEvent(roomName, data);
		}, error -> log.error("Error in taskStateStream: " + error.getMessage()));
	}

	/**
	 taskEventLog에 taskId와 일치하는 room에 존재하는 사용자에게 taskEventLog 데이터 전송
	 */
	@SendEvent(eventPath = "/tasks/{taskId}/log")
	public void sendTasksLogsRoomByTaskId() {
		taskConsumers.getTaskEventLogConsumer().subscribe(data -> {
			log.info("Tasks Log Data send start: " + data);
			String roomName = String.format(TASK_LOG_ROOM_EVENT, data.getTaskId());
			server.getRoomOperations(roomName).sendEvent(roomName, data);
		}, error -> log.error("Error in tasksLogStream: " + error.getMessage()));
	}

	/**
	 * fleetState의 Id와 일치하는 room에 존재하는 사용자에게 fleetState 데이터 전송
	 */
	@SendEvent(eventPath = "/fleets/{fleetStateName}/state")
	public void sendFleetStatesRoomByName() {
		fleetConsumers.getFleetStatesConsumer().subscribe(data -> {
			log.info("Fleet State Data send start: " + data);
			String roomName = String.format(FLEET_STATE_ROOM_EVENT, data.getName());
			server.getRoomOperations(roomName).sendEvent(roomName, data);
		}, error -> log.error("Error in fleetsStateStream: " + error.getMessage()));
	}

	/**
	 * fleetLog의 name과 일치하는 room에 존재하는 사용자에게 fleetLog 데이터 전송
	 */
	@SendEvent(eventPath = "/fleets/{fleetLogName}/log")
	public void sendFleetLogsRoomByName() {
		fleetConsumers.getFleetLogsConsumer().subscribe(data -> {
			log.info("Fleet Log Data send start: " + data);
			String roomName = String.format(FLEET_LOG_ROOM_EVENT, data.getName());
			server.getRoomOperations(roomName).sendEvent(roomName, data);
		}, error -> log.error("Error in fleetsLogStream: " + error.getMessage()));
	}

	@SendEvent(eventPath = "/building_map")
	public void sendBuildingMap() {
		rmfConsumers.getBuildingMapConsumer().subscribe(data -> {
			log.info("Building Map Data send start: {}", data);
			server.getRoomOperations(BUILDING_MAP_ROOM_EVENT).sendEvent(BUILDING_MAP_ROOM_EVENT, data.getData());
		}, error -> log.error("Error in BuildingMapStream: {}", error.getMessage()));
	}

	@SendEvent(eventPath = "/doors/{doorName}/state")
	public void sendDoorState() {
		rmfConsumers.getDoorStateConsumer().subscribe(data -> {
			String roomName = String.format(DOOR_STATE_ROOM_EVENT, data.getId());
			log.info("Door State Data send start: {}", data.getId());
			server.getRoomOperations(roomName).sendEvent(roomName, data.getData());
		}, error -> log.error("Error in DoorStateStream: {}", error.getMessage()));
	}

	@SendEvent(eventPath = "/doors/{doorName}/health")
	public void sendDoorHealth() {
		rmfConsumers.getDoorHealthConsumer().subscribe(data -> {
			String roomName = String.format(DOOR_HEALTH_ROOM_EVENT, data.getId());
			log.info("Door Health Data send start: {}", data.getId());
			server.getRoomOperations(roomName).sendEvent(roomName, data);
		}, error -> log.error("Error in DoorHealthStream: {}", error.getMessage()));
	}

	/**
	 * subscribe, unsubscribe 이벤트에서 클라이언트가 전달하는 데이터
	 */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Message {
		private String room;
	}

	/**
	 * 클라이언트에게 이벤트 성공 여부를 전달하기 위한 데이터
	 */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Success {
		private boolean success;
	}
}
