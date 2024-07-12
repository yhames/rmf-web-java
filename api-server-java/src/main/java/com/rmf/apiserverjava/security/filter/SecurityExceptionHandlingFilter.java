package com.rmf.apiserverjava.security.filter;

import static com.rmf.apiserverjava.global.exception.hadler.GlobalExceptionHandlerAdvice.*;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmf.apiserverjava.global.exception.custom.ForbiddenException;
import com.rmf.apiserverjava.global.exception.custom.UnauthorizedException;
import com.rmf.apiserverjava.global.exception.hadler.GlobalExceptionHandlerAdvice;
import com.rmf.apiserverjava.global.utils.ObjectMapperUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * SecurityExceptionHandlingFilter.
 *
 * <p>
 *	Controller 이전에 발생한 시큐리티 필터 예외를 핸들링하기 위한 필터
 * </p>
 */
@Slf4j
public class SecurityExceptionHandlingFilter extends OncePerRequestFilter {

	private ObjectMapper objectMapper = ObjectMapperUtils.MAPPER;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (UnauthorizedException ex) {
			log.error(ex.getMessage());
			handleException(response, ex.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
		} catch (ForbiddenException ex) {
			log.error(ex.getMessage());
			handleException(response, ex.getMessage(), HttpServletResponse.SC_FORBIDDEN);
		} catch (RuntimeException ex) {
			log.error(ex.getMessage());
			handleException(response, UNHANDLED_ERROR_MSG, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			handleException(response, UNHANDLED_ERROR_MSG, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void handleException(HttpServletResponse response, String msg, int status) throws IOException {
		GlobalExceptionHandlerAdvice.ErrorResponse errorResponse;
		errorResponse = new GlobalExceptionHandlerAdvice.ErrorResponse(msg);
		response.setStatus(status);
		response.setContentType("application/json");
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}

