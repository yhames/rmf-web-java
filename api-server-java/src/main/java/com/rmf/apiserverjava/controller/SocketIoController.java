package com.rmf.apiserverjava.controller;

import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.rmf.apiserverjava.controller.impl.SocketIoControllerImpl;

/**
 * SocketIoController.
 *
 * <p>
 *	API Client와 연계되는 socket.io 이벤트를 정의합니다.
 * </p>
 */
public interface SocketIoController {

	/**
	 * 연결 이벤트 리스너 등록
	 *
	 */
	ConnectListener listenConnected();

	/**
	 * 연결 해제 이벤트 리스너 등록
	 *
	 */
	DisconnectListener listenDisconnected();

	/**
	 * 구독 이벤트 리스너 등록
	 *
	 */
	DataListener<SocketIoControllerImpl.Message> listenSubscribe();

	/**
	 * 구독 해제 이벤트 리스너 등록
	 *
	 */
	DataListener<SocketIoControllerImpl.Message> listenUnsubscribe();

	/**
	 * Alerts 룸에 존재하는 사용자에게 알림을 전송
	 *
	 */
	void sendAlertsRoom();

	/**
	 * Beacons 룸에 존재하는 사용자에게 알림을 전송
	 *
	 */
	void sendBeaconsRoom();

	/**
	 * TaskState 룸에 존재하는 사용자에게 taskState를 전송
	 *
	 */
	void sendTaskStatesRoomByTaskId();

	/**
	 * TasksLog 룸에 존재하는 사용자에게 TaskEventLog를 전송
	 *
	 */
	void sendTasksLogsRoomByTaskId();

	/**
	 * FleetStates 룸에 존재하는 사용자에게 FleetStates를 전송
	 *
	 */
	void sendFleetStatesRoomByName();

	/**
	 * FleetsLog 룸에 존재하는 사용자에게 FleetLog를 전송
	 *
	 */
	void sendFleetLogsRoomByName();
}
