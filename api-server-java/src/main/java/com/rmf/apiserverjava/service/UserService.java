package com.rmf.apiserverjava.service;

import java.util.List;
import java.util.Optional;

import com.rmf.apiserverjava.dto.users.ChangePwReqDto;
import com.rmf.apiserverjava.dto.users.CreateUserReqDto;
import com.rmf.apiserverjava.dto.users.UserQueryRequestDto;
import com.rmf.apiserverjava.entity.users.User;

/**
 * User와 관련된 비즈니스 로직을 정의한다.
 * */
public interface UserService {

	/**
	 * 쿼리파라미터를 기준으로 User의 목록을 조회한다.
	 * */
	List<User> getAllUsers(UserQueryRequestDto userQueryRequestDto);

	/**
	 * username을 기준으로 User를 조회한다.
	 * */
	Optional<User> getUser(String username);

	/**
	 * 새로운 User를 추가한다.
	 * */
	User createUser(CreateUserReqDto createUserReqDto);

	/**
	 * username을 기준으로 User를 삭제한다.
	 * */
	void deleteUser(String username);

	/**
	 * username을 기준으로 User의 비밀번호를 변경한다.
	 * */
	void changePassword(String username, ChangePwReqDto changePwReqDto);

	/**
	 * username을 기준으로 User의 이메일을 변경한다.
	 * */
	void changeEmail(String before, String newEmail);

	/**
	 * email 인증 로직을 정의한다.
	 * */
	void emailVerification(User user, String code);

	/**
	 * User를 저장한다.
	 * */
	void saveUser(User user);
}
