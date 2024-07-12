package com.rmf.apiserverjava.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rmf.apiserverjava.dto.page.PaginationDto;
import com.rmf.apiserverjava.dto.users.ChangePwReqDto;
import com.rmf.apiserverjava.dto.users.CreateUserReqDto;
import com.rmf.apiserverjava.dto.users.UserQueryRequestDto;
import com.rmf.apiserverjava.entity.users.Email;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.exception.custom.BusinessException;
import com.rmf.apiserverjava.global.exception.custom.InvalidClientArgumentException;
import com.rmf.apiserverjava.global.exception.custom.NotFoundException;
import com.rmf.apiserverjava.global.utils.JwtUtil;
import com.rmf.apiserverjava.repository.EmailRepository;
import com.rmf.apiserverjava.repository.UserQueryRepository;
import com.rmf.apiserverjava.repository.UserRepository;
import com.rmf.apiserverjava.security.UserSession;
import com.rmf.apiserverjava.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * UserServiceImpl
 *
 * <p>
 *	UserService의 구현체
 * </p>
 */

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserQueryRepository userQueryRepository;
	private final EmailRepository emailRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserSession userSession;
	private final JwtUtil jwtUtil;

	public static final String ID_EXIST_ERROR_MSG = "ID already exists.";
	public static final String EMAIL_EXIST_ERROR_MSG = "Email already exists.";
	public static final String ID_NOT_FOUND_MSG = "ID %s not found";
	public static final String OLD_PASSWORD_ERROR_MSG = "Old password is incorrect.";
	public static final String CONFIRM_PASSWORD_ERROR_MSG = "New password and confirm password are different.";
	public static final String PASSWORD_CHANGE_ERROR_MSG = "Password change failed: ";
	public static final String USER_CREATE_ERROR_MSG = "User create failed: ";

	private static final String DEFAULT_PASSWORD = "1234";

	/**
	* 쿼리파라미터를 기준으로 User의 목록을 조회한다.
	* */
	@Override
	public List<User> getAllUsers(UserQueryRequestDto userQueryRequestDto) {
		PaginationDto paginationDto = PaginationDto.builder()
			.limit(userQueryRequestDto.getLimit())
			.offset(userQueryRequestDto.getOffset())
			.orderBy(userQueryRequestDto.getOrderBy())
			.build();
		return userQueryRepository.findAllUserByQuery(
			userQueryRequestDto.getUsername(),
			userQueryRequestDto.getIsAdmin(),
			paginationDto);
	}

	/**
	* username을 기준으로 User를 조회한다.
	* */
	@Override
	public Optional<User> getUser(String username) {
		return userRepository.findById(username);
	}

	/**
	 * 이메일 전송
	* */
	public boolean sendEmail(String email, String password) {
		//TODO : 임시비밀번호 발급 후 이메일로 전송
		return true;
	}

	/**
	* 새로운 유저를 생성한다.
	 * email을 먼저 생성하고, user를 생성한다.
	* */
	@Override
	@Transactional
	public User createUser(CreateUserReqDto createUserReqDto) {
		if (userRepository.existsById(createUserReqDto.getUsername())) {
			throw new InvalidClientArgumentException(ID_EXIST_ERROR_MSG);
		}
		if (emailRepository.existsByEmail(createUserReqDto.getEmail())) {
			throw new InvalidClientArgumentException(EMAIL_EXIST_ERROR_MSG);
		}
		Email email = Email.builder()
			.email(createUserReqDto.getEmail())
			.isVerified(true)
			.build();
		User user = User.builder()
			.username(createUserReqDto.getUsername())
			.isAdmin(createUserReqDto.getIsAdmin())
			.password(bCryptPasswordEncoder.encode(DEFAULT_PASSWORD))
			.email(email)
			.build();
		try {
			emailRepository.save(email);
			User saved = userRepository.save(user);
			sendEmail(createUserReqDto.getEmail(), DEFAULT_PASSWORD);
			return saved;
		} catch (Exception e) {
			throw new BusinessException(USER_CREATE_ERROR_MSG + e.getMessage());
		}
	}

	/**
	 * username을 기준으로 User를 삭제한다.
	 * 삭제시 UserSession에서도 삭제하고, dormantTime을 갱신한다.
	 */
	@Override
	public void deleteUser(String username) {
		User user = userRepository.findById(username).orElseThrow(()
			-> new NotFoundException(String.format(ID_NOT_FOUND_MSG, username)));
		userRepository.delete(user);
		userSession.removeSession(username);
		jwtUtil.renewDormantTime(username);
	}

	/**
	 * username을 기준으로 User의 비밀번호를 변경한다.
	 * old password가 일치하지 않으면 예외를 발생시킨다.
	 * new password와 confirm password가 일치하지 않으면 예외를 발생시킨다.
	 */
	@Override
	@Transactional
	public void changePassword(String username, ChangePwReqDto changePwReqDto) {
		User user = userRepository.findById(username).orElseThrow(()
			-> new NotFoundException(String.format(ID_NOT_FOUND_MSG, username)));
		if (!bCryptPasswordEncoder.matches(changePwReqDto.getCurrentPassword(), user.getPassword())) {
			throw new InvalidClientArgumentException(OLD_PASSWORD_ERROR_MSG);
		}
		if (!changePwReqDto.getNewPassword().equals(changePwReqDto.getConfirmPassword())) {
			throw new InvalidClientArgumentException(CONFIRM_PASSWORD_ERROR_MSG);
		}
		try {
			user.setPassword(bCryptPasswordEncoder.encode(changePwReqDto.getNewPassword()));
		} catch (Exception e) {
			throw new BusinessException(PASSWORD_CHANGE_ERROR_MSG + e.getMessage());
		}
	}

	@Override
	public void changeEmail(String before, String newEmail) {
		//TODO: 이메일 변경 로직 구현
	}

	@Override
	public void emailVerification(User user, String code) {
		//TODO: 이메일 인증 로직 구현
	}

	@Override
	@Transactional
	public void saveUser(User user) {
		userRepository.save(user);
	}
}
