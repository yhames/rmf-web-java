package com.rmf.apiserverjava.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;

/**
 * SocketIoConfig.
 */
@Configuration
public class SocketIoConfig {

	@Autowired
	AuthorizationListener authorizationListener;

	@Value("${socketio.server.hostname}")
	private String hostname;

	@Value("${socketio.server.port}")
	private int port;

	/**
	 * Tomcat 서버와 별도로 돌아가는 netty 서버를 생성
	 */
	@Bean
	public SocketIOServer socketIoServer() {
		com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
		config.setHostname(hostname);
		config.setPort(port);
		config.setAuthorizationListener(authorizationListener);
		return new SocketIOServer(config);
	}
}
