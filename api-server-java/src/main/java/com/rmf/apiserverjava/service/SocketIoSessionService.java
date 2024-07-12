package com.rmf.apiserverjava.service;

import com.corundumstudio.socketio.SocketIOClient;

/**
 * SocketIoSessionService.
 *
 * <p>
 *	Socket IO의 클라이언트 연결을 제어하는 서비스 인터페이스
 * </p>
 */
public interface SocketIoSessionService {

	/**
	 * 동일 클라이언트의 신규 연결 여부를 반환
	 */
	boolean isNewConnection(SocketIOClient client);

	/**
	 * 세션, 토큰 만료를 모니터링해 연결을 해제할 클라이언트를 등록
	 */
	void manageClient(SocketIOClient client);

	/**
	 * 클라이언트 연결 모니터링을 중단
	 */
	void stopManageClient(SocketIOClient client);
}
