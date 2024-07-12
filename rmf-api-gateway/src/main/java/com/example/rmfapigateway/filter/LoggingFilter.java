package com.example.rmfapigateway.filter;

import java.net.URI;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * LoggingFilter.
 *
 * <p>
 *	API Request, Response 요청에 대한 로깅을 수행
 * </p>
 * @since           : 2024/04/20
 */
@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

	private final static String REQUEST_LOG_MSG
		= "\u001B[32mRequest  Filter : [{}] - Request ID: {} - Routed to: {}:{}{}\u001B[0m";
	private final static String RESPONSE_LOG_MSG
		= "\u001B[35mResponse Filter : [{}] - Response code: {} for Request ID: {}\u001B[0m";

	public LoggingFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return new OrderedGatewayFilter((exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			ServerHttpResponse response = exchange.getResponse();
			URI routeUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
			String requestId = request.getId();

			requestLog(request, config, routeUri);
			return chain.filter(exchange).then(Mono.fromRunnable(() -> responseLog(response, requestId, config)));
		}, Ordered.LOWEST_PRECEDENCE);
	}

	private void requestLog(ServerHttpRequest request, Config config, URI routeUri) {
		String baseMessage = config.getBaseMessage();
		String requestId = request.getId();
		String routeHost = routeUri.getHost();
		int routePort = routeUri.getPort();
		String path = request.getPath().toString();

		if (config.isPreLogger()) {
			log.info(REQUEST_LOG_MSG, baseMessage, requestId, routeHost, routePort, path);
		}
	}

	private void responseLog(ServerHttpResponse response, String requestId, Config config) {
		HttpStatusCode statusCode = response.getStatusCode();

		if (config.isPostLogger()) {
			log.info(RESPONSE_LOG_MSG, config.getBaseMessage(), statusCode, requestId);
		}
	}

	@Getter
	@Setter
	public static class Config {
		private String baseMessage;
		private boolean preLogger;
		private boolean postLogger;
	}
}
