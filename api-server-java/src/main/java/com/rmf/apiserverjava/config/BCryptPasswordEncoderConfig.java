package com.rmf.apiserverjava.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * BCryptPasswordEncoderConfig.
 *
 * <p>
 *     암호화를 위한 BCryptPasswordEncoder Bean 설정
 * </p>
 */
@Configuration
public class BCryptPasswordEncoderConfig {
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
