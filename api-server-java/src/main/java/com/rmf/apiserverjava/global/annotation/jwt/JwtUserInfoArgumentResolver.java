package com.rmf.apiserverjava.global.annotation.jwt;

import java.util.Optional;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.rmf.apiserverjava.dto.jwt.JwtUserInfoDto;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.exception.custom.UnauthorizedException;
import com.rmf.apiserverjava.global.utils.JwtUtil;
import com.rmf.apiserverjava.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * JwtUserInfoArgumentResolver.
 *
 * <p>
 *     JwtUserInfo 어노테이션을 사용한 파라미터를 해석하는 Resolver. Access Token에 담긴 사용자 정보를 반환한다.
 * </p>
 */
@RequiredArgsConstructor
public class JwtUserInfoArgumentResolver implements HandlerMethodArgumentResolver {
	private static final String USER_NOT_EXIST = "사용자가 존재하지 않습니다.";
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean hasAnnotation = parameter.hasParameterAnnotation(JwtUserInfo.class);
		boolean hasDtoType = JwtUserInfoDto.class.isAssignableFrom(parameter.getParameterType());
		return hasAnnotation && hasDtoType;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
		HttpServletResponse response = (HttpServletResponse)webRequest.getNativeResponse();
		String accessToken = jwtUtil.getAccessTokenFromCookies(request.getCookies());
		String username = jwtUtil.getUsername(accessToken);
		Optional<User> user = userRepository.findById(username);
		if (user.isEmpty()) {
			jwtUtil.expireAccessToken(response);
			jwtUtil.expireRefreshToken(response);
			throw new UnauthorizedException(USER_NOT_EXIST);
		}
		JwtUserInfoDto jwtUserInfoDto = JwtUserInfoDto.builder().username(user.get().getUsername()).build();
		return jwtUserInfoDto;
	}
}
