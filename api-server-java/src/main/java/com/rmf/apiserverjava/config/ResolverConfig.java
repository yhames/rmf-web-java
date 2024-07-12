package com.rmf.apiserverjava.config;

import static com.rmf.apiserverjava.global.constant.ProfileConstant.*;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.rmf.apiserverjava.global.annotation.jwt.JwtUserInfoArgumentResolver;
import com.rmf.apiserverjava.global.utils.JwtUtil;
import com.rmf.apiserverjava.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * ResolverConfig.
 *
 * <p>
 *	자체 정의한 Resolver를 등록하는 Config 클래스
 * </p>
 */
@Profile({MAIN, TEST_WITH_SECURITY})
@Configuration
@RequiredArgsConstructor
public class ResolverConfig implements WebMvcConfigurer {
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new JwtUserInfoArgumentResolver(jwtUtil, userRepository));
	}
}
