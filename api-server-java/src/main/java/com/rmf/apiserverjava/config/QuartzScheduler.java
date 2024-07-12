package com.rmf.apiserverjava.config;

import java.util.List;
import java.util.Optional;

import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTask;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.global.exception.custom.BusinessException;
import com.rmf.apiserverjava.repository.ScheduledTaskRepository;
import com.rmf.apiserverjava.service.QuartzService;
import com.rmf.apiserverjava.service.UserService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

/**
 * QuartzScheduler
 *
 * <p>
 *     Quartz Scheduler를 초기화하고 종료하는 클래스
 * </p>
 */
@Slf4j
@Component
public class QuartzScheduler {

	private static final String QUARTZ_INIT_FAIL = "Quartz Scheduler 실행에 실패했습니다: ";

	private static final String QUARTZ_START_UP_COUNT = "Quartz Scheduler: {} 개의 Task를 로드했습니다.";

	private static final String QUARTZ_SHUT_DOWN = "Quartz Scheduler를 종료합니다.";

	private final SchedulerFactoryBean schedulerFactoryBean;

	private final ScheduledTaskRepository scheduledTaskRepository;

	private final QuartzService quartzService;

	private final UserService userService;

	/**
	 * QuartzScheduler Constructor
	 *
	 * <p>
	 *     QuartzScheduler를 실행합니다.
	 * </p>
	 */
	public QuartzScheduler(ScheduledTaskRepository scheduledTaskRepository, QuartzService quartzService,
		SchedulerFactoryBean schedulerFactoryBean, UserService userService) {
		this.scheduledTaskRepository = scheduledTaskRepository;
		this.quartzService = quartzService;
		this.schedulerFactoryBean = schedulerFactoryBean;
		this.userService = userService;
		try {
			schedulerFactoryBean.getScheduler().start();
		} catch (Exception e) {
			throw new BusinessException(QUARTZ_INIT_FAIL);
		}
	}

	/**
	 * onStart
	 *
	 * <p>
	 *     DB에 저장된 ScheduledTask를 로드하여 스케줄링합니다.
	 * </p>
	 */
	@PostConstruct
	private void onStart() {
		int scheduledCount = 0;
		try {
			List<ScheduledTask> scheduledTasks = scheduledTaskRepository.findAll();
			log.info("ScheduledTask: {}", scheduledTasks.size());
			for (ScheduledTask scheduledTask : scheduledTasks) {
				Optional<User> user = userService.getUser(scheduledTask.getCreatedBy());
				if (user.isEmpty()) {
					log.error("User Not Found: ScheduledTask Id: {}", scheduledTask.getId());
					continue;
				}
				quartzService.scheduleTask(scheduledTask);
				scheduledCount++;
			}
		} catch (SchedulerException e) {
			throw new BusinessException(QUARTZ_INIT_FAIL + e.getMessage());
		}
		log.info(QUARTZ_START_UP_COUNT, scheduledCount);
	}

	/**
	 * onExit
	 *
	 * <p>
	 *     Quartz Scheduler를 종료합니다.
	 * </p>
	 */
	@PreDestroy
	public void onExit() throws SchedulerException {
		log.info(QUARTZ_SHUT_DOWN);
		schedulerFactoryBean.getScheduler().shutdown();
	}
}
