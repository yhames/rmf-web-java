package com.rmf.apiserverjava.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.rmf.apiserverjava.websocket.FleetAdapterHandler;

/**
 * WebSocketConfig.
 *
 * <p>
 *	WebSocket 설정 및 라우팅을 정의
 * </p>
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	private static final String FLEET_ADAPTER_PATH = "/_internal";
	private final FleetAdapterHandler fleetAdapterHandler;

	public WebSocketConfig(FleetAdapterHandler fleetAdapterHandler) {
		this.fleetAdapterHandler = fleetAdapterHandler;
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		// TODO: origin 관련 보안 설정 필요
		registry.addHandler(fleetAdapterHandler, FLEET_ADAPTER_PATH).setAllowedOrigins("*");
	}
}
