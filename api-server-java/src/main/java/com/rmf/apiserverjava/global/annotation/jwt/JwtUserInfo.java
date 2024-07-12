package com.rmf.apiserverjava.global.annotation.jwt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JwtUserInfo.
 *
 * <p>
 *	JWT에서 유저 정보를 추출하기 위한 어노테이션
 * </p>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface JwtUserInfo {

}
