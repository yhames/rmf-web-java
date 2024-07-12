package com.rmf.apiserverjava.service.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.corundumstudio.socketio.SocketIOClient;
import com.rmf.apiserverjava.controller.impl.SocketIoControllerImpl;
import com.rmf.apiserverjava.global.utils.JwtUtil;
import com.rmf.apiserverjava.security.UserSession;
import com.rmf.apiserverjava.service.SocketIoSessionService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * SocketIoSessionServiceImpl.
 *
 * <p>
 *	SocketIoSessionService의 구현체
 * </p>
 */
@Service
@Slf4j
public class SocketIoSessionServiceImpl implements SocketIoSessionService {
	private static final String EXPIRE_EVENT = "disconnect";
	private final ScheduledExecutorService scheduler;
	private final ConcurrentHashMap<String, ScheduledFuture<?>> scheduledTasks;
	private final ConcurrentHashMap<String, String> clientInfo;
	private final ConcurrentHashMap<String, ConcurrentHashMap<SocketIOClient, Boolean>> connectUserClients;

	private final JwtUtil jwtUtil;
	private final UserSession userSession;

	public SocketIoSessionServiceImpl(JwtUtil jwtUtil, UserSession userSession) {
		this.jwtUtil = jwtUtil;
		this.userSession = userSession;
		scheduler = Executors.newScheduledThreadPool(10);
		clientInfo = new ConcurrentHashMap<>();
		scheduledTasks = new ConcurrentHashMap<>();
		connectUserClients = new ConcurrentHashMap<>();
	}

	/**
	 * 세션 만료 여부를 확인하는 스케줄러를 1분 간격으로 실행
	 */
	@PostConstruct
	private void scheduleSessionChecks() {
		scheduler.scheduleAtFixedRate(this::checkExpiredSession, 0, 1, TimeUnit.MINUTES);
	}

	/**
	 * 유저별로 만료된 세션 연결을 해제
	 */
	private void checkExpiredSession() {
		connectUserClients.forEach((username, clients) -> {
			if (userSession.isExpired(username)) {
				clients.keySet().forEach(client -> scheduleDisconnection(client, 0));
			}
		});
	}

	/**
	 * 동일 클라이언트의 신규 연결 여부를 반환
	 */
	public boolean isNewConnection(SocketIOClient client) {
		String cookie = client.getHandshakeData().getHttpHeaders().get("Cookie");
		String accessToken = jwtUtil.getAccessTokenFromHeader(cookie);
		String username = jwtUtil.getUsername(accessToken);
		return clientInfo.putIfAbsent(client.getSessionId().toString(), username) != null;
	}

	/**
	 * 세션, 토큰 만료를 모니터링해 연결을 해제할 클라이언트를 등록
	 */
	public void manageClient(SocketIOClient client) {
		String cookie = client.getHandshakeData().getHttpHeaders().get("Cookie");
		String accessToken = jwtUtil.getAccessTokenFromHeader(cookie);
		String username = jwtUtil.getUsername(accessToken);
		long expirationTime = jwtUtil.getTokenExpirationTime(accessToken);
		expirationTime = Math.max(0, expirationTime - 60000);

		scheduleDisconnection(client, expirationTime);
		addUserClient(client, username);
	}

	/**
	 * 클라이언트 관련 스케줄러 및 정보를 삭제하여 연결 모니터링을 중단
	 */
	public void stopManageClient(SocketIOClient client) {
		removeExistSchedule(client);
		removeUserClient(client);
		clientInfo.remove(client.getSessionId().toString());
	}

	/**
	 * 클라이언트의 연결 해제를 예약
	 */
	private void scheduleDisconnection(SocketIOClient client, long delay) {
		removeExistSchedule(client);

		ScheduledFuture<?> newTask = scheduler.schedule(() -> {
			if (client.isChannelOpen()) {
				client.disconnect();
				log.info("send expire event");
				client.sendEvent(EXPIRE_EVENT, new SocketIoControllerImpl.Success(true));
			}
		}, delay, TimeUnit.MILLISECONDS);

		scheduledTasks.put(client.getSessionId().toString(), newTask);
	}

	/**
	 * 유저별 클라이언트 목록에서 SocketIOClient 삭제한다. 유저별 클라이언트 목록이 비어있으면 유저 정보를 삭제한다.
	 */
	private void removeUserClient(SocketIOClient client) {
		String username = clientInfo.getOrDefault(client.getSessionId().toString(), "");
		if (connectUserClients.get(username) != null) {
			connectUserClients.get(username).remove(client);
			if (connectUserClients.get(username).isEmpty()) {
				connectUserClients.remove(username);
			}
		}
	}

	/**
	 * 클라이언트의 아직 진행중이 아닌 예정된 스케줄을 삭제
	 */
	private void removeExistSchedule(SocketIOClient client) {
		ScheduledFuture<?> task = scheduledTasks.remove(client.getSessionId().toString());
		if (task != null) {
			task.cancel(false);
		}
	}

	/**
	 * 유저별 클라이언트 목록에 SocketIOClient 추가
	 */
	private void addUserClient(SocketIOClient client, String username) {
		connectUserClients.putIfAbsent(username, new ConcurrentHashMap<>());
		connectUserClients.get(username).put(client, true);
	}

}
