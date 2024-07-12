package com.rmf.apiserverjava.global.exception.hadler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.rmf.apiserverjava.global.exception.custom.BusinessException;
import com.rmf.apiserverjava.global.exception.custom.DateTimeParseException;
import com.rmf.apiserverjava.global.exception.custom.ForbiddenException;
import com.rmf.apiserverjava.global.exception.custom.InvalidClientArgumentException;
import com.rmf.apiserverjava.global.exception.custom.JsonProcessingException;
import com.rmf.apiserverjava.global.exception.custom.NotFoundException;
import com.rmf.apiserverjava.global.exception.custom.SchedulerParseException;
import com.rmf.apiserverjava.global.exception.custom.UnauthorizedException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * GlobalExceptionHandlerAdvice.
 *
 * <p>
 *	컨트롤러에서 반환된 예외의 전역 처리를 위한 클래스
 * </p>
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandlerAdvice {

	public static final String INTERNAL_SERVER_ERROR_MSG = "Internal Server Error";
	public static final String UNHANDLED_ERROR_MSG = "Unhandled Error";

	private static final String BAD_REQUEST_ERROR_MSG = "Invalid Request";

	@Getter
	public static class ErrorResponse {
		private final String detail;

		public ErrorResponse(String detail) {
			this.detail = detail;
		}
	}

	@ExceptionHandler({InvalidClientArgumentException.class})
	public ResponseEntity<ErrorResponse> validException(InvalidClientArgumentException ex) {
		log.error(ex.getMessage());
		ErrorResponse response = new ErrorResponse(ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({DateTimeParseException.class})
	public ResponseEntity<ErrorResponse> validException(DateTimeParseException ex) {
		log.error(ex.getMessage());
		ErrorResponse response = new ErrorResponse(ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({NotFoundException.class})
	public ResponseEntity<ErrorResponse> validException(NotFoundException ex) {
		log.error(ex.getMessage());
		ErrorResponse response = new ErrorResponse(ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({JsonProcessingException.class})
	public ResponseEntity<ErrorResponse> validException(JsonProcessingException ex) {
		log.error(ex.getMessage());
		ErrorResponse response = new ErrorResponse(ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({BusinessException.class})
	public ResponseEntity<ErrorResponse> validException(BusinessException ex) {
		log.error(ex.getMessage());
		ErrorResponse response = new ErrorResponse(INTERNAL_SERVER_ERROR_MSG);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({MethodArgumentNotValidException.class})
	public ResponseEntity<ErrorResponse> validException(MethodArgumentNotValidException ex) {
		ErrorResponse response = new ErrorResponse(BAD_REQUEST_ERROR_MSG);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({SchedulerParseException.class})
	public ResponseEntity<ErrorResponse> validException(SchedulerParseException ex) {
		log.error(ex.getMessage());
		ErrorResponse response = new ErrorResponse(ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({MissingServletRequestParameterException.class})
	public ResponseEntity<ErrorResponse> validException(MissingServletRequestParameterException ex) {
		log.error(ex.getMessage());
		ErrorResponse response = new ErrorResponse(ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ForbiddenException.class})
	public ResponseEntity<ErrorResponse> validException(ForbiddenException ex) {
		log.error(ex.getMessage());
		ErrorResponse response = new ErrorResponse(ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler({UnauthorizedException.class})
	public ResponseEntity<ErrorResponse> validException(UnauthorizedException ex) {
		log.error(ex.getMessage());
		ErrorResponse response = new ErrorResponse(ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler({NoResourceFoundException.class})
	public ResponseEntity<ErrorResponse> validException(NoResourceFoundException ex) {
		log.error(ex.getMessage());
		ErrorResponse response = new ErrorResponse(ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({RuntimeException.class})
	public ResponseEntity<ErrorResponse> validException(RuntimeException ex) {
		log.error(ex.getMessage());
		ErrorResponse response = new ErrorResponse(UNHANDLED_ERROR_MSG);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({Exception.class})
	public ResponseEntity<ErrorResponse> validException(Exception ex) {
		log.error(ex.getMessage());
		ErrorResponse response = new ErrorResponse(UNHANDLED_ERROR_MSG);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
