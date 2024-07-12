package com.rmf.apiserverjava.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import com.rmf.apiserverjava.dto.users.CreateUserReqDto;
import com.rmf.apiserverjava.dto.users.UserQueryRequestDto;
import com.rmf.apiserverjava.dto.users.UserResponseDto;

/**
 * AdminController.
 *
 * <p>
 *	Admin 권한을 지닌 유저가 처리하는 컨트롤러 인터페이스
 * </p>
 */
public interface AdminController {

	/**
	 * 모든 회원의 목록을 조회한다.
	 */
	List<String> getAllUsers(String username, Boolean isAdmin,
		Integer limit, Integer offset, String orderBy);

	/**
	 * 회원 정보를 조회한다.
	 */
	UserResponseDto getUser(String username);


	/**
	 * 새로운 회원을 추가한다.
	 */
	ResponseEntity<UserResponseDto> createUser(CreateUserReqDto createUserReqDto);


	/**
	 * 회원을 삭제한다.
	 */
	ResponseEntity<Void> deleteUser(String username);

	/**
	 * 회원의 역할을 모두 조회한다
	 */
	List<String> getUserRoles();
}
