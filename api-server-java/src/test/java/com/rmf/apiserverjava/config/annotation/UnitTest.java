package com.rmf.apiserverjava.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UnitTest.
 *
 * <p>
 *	단위 테스트 환경의 의존성 관리를 위한 어노테이션.
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag(TestType.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
public @interface UnitTest {
}
