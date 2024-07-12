package com.rmf.apiserverjava.config;

/**
 * SecuritySisabledTestConfig.
 *
 * <p>
 *	시큐리티를 제외한 통합 테스트를 위한 Bean
 * </p>
 */

import static com.rmf.apiserverjava.global.constant.ProfileConstant.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Profile(TEST_WITHOUT_SECURITY)
@EnableWebSecurity
@Configuration
public class SecurityDisabledTestConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// 토큰 기반 인증에 따른 설정
		http
			.logout((logout) -> logout.disable())
			.csrf((auth) -> auth.disable())
			.formLogin((auth) -> auth.disable())
			.httpBasic((auth) -> auth.disable())
			.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// 경로별 인가 작업을 수행
		http
			.authorizeHttpRequests((requests) -> requests
				.anyRequest().permitAll());

		return http.build();
	}
}
