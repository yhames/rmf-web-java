package com.rmf.apiserverjava.config;

import static com.rmf.apiserverjava.global.constant.ProfileConstant.*;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.rmf.apiserverjava.global.exception.custom.ForbiddenException;
import com.rmf.apiserverjava.global.utils.JwtUtil;
import com.rmf.apiserverjava.security.SecurityRole;
import com.rmf.apiserverjava.security.UserSession;
import com.rmf.apiserverjava.security.filter.ActiveUserFilter;
import com.rmf.apiserverjava.security.filter.JwtAuthFilter;
import com.rmf.apiserverjava.security.filter.LoginFilter;
import com.rmf.apiserverjava.security.filter.SecurityExceptionHandlingFilter;
import com.rmf.apiserverjava.service.AuthService;
import com.rmf.apiserverjava.service.JwtService;

import lombok.RequiredArgsConstructor;

/**
 * SecurityConfig.
 *
 * <p>
 *     스프링 시큐리티 관련 설정 및 Bean 관리를 위한 클래스
 * </p>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Profile(value = {MAIN, TEST_WITH_SECURITY})
public class SecurityConfig {
	public static final String FORBIDDEN_MESSAGE = "Forbidden Access";
	private final AuthenticationConfiguration authenticationConfiguration;
	private final JwtUtil jwtUtil;
	private final JwtService jwtService;
	private final AuthService authService;
	private final UserSession userSession;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	/**
	 * CORS 설정
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8090"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	/**
	 * 스프링 시큐리티 필터 체인 및 경로별 인가 설정
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// 토큰 기반 인증에 따른 설정
		http
			.logout((logout) -> logout.disable())
			.csrf((auth) -> auth.disable())
			.formLogin((auth) -> auth.disable())
			.httpBasic((auth) -> auth.disable())
			.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// 인증 관련 필터 추가
		http
			.addFilterBefore(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, jwtService,
				userSession), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new ActiveUserFilter(jwtUtil, userSession, authService), LoginFilter.class)
			.addFilterBefore(new JwtAuthFilter(jwtUtil), ActiveUserFilter.class);

		// 예외처리 관련 필터 등록
		http
			.addFilterBefore(new SecurityExceptionHandlingFilter(), JwtAuthFilter.class)
			.exceptionHandling((exception) -> exception
				.authenticationEntryPoint((request, response, authException) -> {
					throw new ForbiddenException(FORBIDDEN_MESSAGE);
				}));

		// 경로별 인가 작업을 수행
		http
			.cors((cors) -> cors.configurationSource(corsConfigurationSource()))
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/login").permitAll()
				.requestMatchers("/token/refresh").permitAll()
				.requestMatchers("/admin**", "/admin/**").hasRole(SecurityRole.ROLE_ADMIN.value)
				.requestMatchers("_internal").permitAll() // TODO: 통합 이후 Admin 권한으로 변경
				.requestMatchers("/swagger-ui/**", "/swagger-ui**", "/v3/api-docs/**", "/v3/api-docs**",
					"/api-docs", "/api-docs/swagger-config").permitAll()
				.requestMatchers("/mock/dummy").permitAll() // TODO: 통합 이후 Admin 권한으로 변경
				.requestMatchers("/error**").permitAll()
				.requestMatchers("/error/**").permitAll()
				.requestMatchers("/mock/image/**").permitAll()
				.anyRequest().authenticated());

		return http.build();
	}

}
