package com.rmf.apiserverjava.security.userdetails;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * CustomUserDetailsService.
 *
 * <p>
 *     Spring Security 로그인시 사용되는 UserDetailsService의 구현체
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	/**
	 * 사용자 이름을 기반으로 사용자 정보를 조회
	 */
	@Override
	public CustomUserDetails loadUserByUsername(String username) {
		Optional<User> user = userRepository.findById(username);
		if (user.isEmpty()) {
			return null;
		}
		return CustomUserDetails.MapStruct.INSTANCE.toDto(user.get());
	}
}
