package com.rmf.apiserverjava.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * QuartzConfig
 *
 * <p>
 *     Quartz Scheduler 설정 클래스
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

	private static final String QUARTZ_INIT_FAIL = "Quartz Scheduler 초기화에 실패했습니다.";

	private final AutowiringSpringBeanJobFactory autowiringSpringBeanJobFactory;

	/**
	 * schedulerFactoryBean
	 *
	 * <p>
	 *     SchedulerFactoryBean 빈 등록.
	 *     Job 클래스에 의존성 주입을 위해 JobFactory를 설정합니다.
	 * </p>
	 */
	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		schedulerFactoryBean.setJobFactory(autowiringSpringBeanJobFactory);
		return schedulerFactoryBean;
	}

	/**
	 * AutowiringSpringBeanJobFactory
	 *
	 * <p>
	 *     Job 클래스에 의존성 주입을 위한 JobFactory
	 * </p>
	 */
	@Component
	@RequiredArgsConstructor
	public static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory {

		private final AutowireCapableBeanFactory beanFactory;

		@Override
		protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
			final Object job = super.createJobInstance(bundle);
			beanFactory.autowireBean(job);	// Job 클래스에 의존성 주입
			return job;
		}
	}
}
