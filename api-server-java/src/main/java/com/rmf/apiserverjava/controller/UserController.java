package com.rmf.apiserverjava.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.rmf.apiserverjava.dto.jwt.JwtUserInfoDto;
import com.rmf.apiserverjava.dto.users.ChangePwReqDto;
import com.rmf.apiserverjava.dto.users.UserResponseDto;

import jakarta.servlet.http.HttpServletRequest;

/**
 * UserController.
 *
 * <p>
 *	User 관련 요청을 처리하는 컨트롤러 인터페이스
 * </p>
 */
public interface UserController {

	/**
	 * 사용자가 자신의 정보를 조회한다.
	 */
	UserResponseDto getMyInfo(JwtUserInfoDto jwtUserInfoDto);

	/**
	 * 사용자가 자신의 비밀번호를 변경한다.
	 */
	ResponseEntity<Void>  changeMyPassword(JwtUserInfoDto jwtUserInfoDto, ChangePwReqDto changePwReqDto);

	/**
	 * 사용자가 자신의 이메일을 변경한다.
	 */
	ResponseEntity<Void>  changeMyEmail(String before, String after);
}
