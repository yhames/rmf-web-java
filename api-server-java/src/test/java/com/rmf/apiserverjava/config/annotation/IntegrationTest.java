package com.rmf.apiserverjava.config.annotation;

import static com.rmf.apiserverjava.global.constant.ProfileConstant.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * IntegrationTest.
 *
 * <p>
 *	통합 테스트 환경의 의존성 관리를 위한 어노테이션. 스프링 시큐리티를 사용하지 않는다.
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@Tag(TestType.INTEGRATION_TEST)
@ActiveProfiles(TEST_WITHOUT_SECURITY)
public @interface IntegrationTest {
}
