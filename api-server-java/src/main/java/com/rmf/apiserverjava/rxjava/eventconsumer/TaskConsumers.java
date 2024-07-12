package com.rmf.apiserverjava.rxjava.eventconsumer;

import java.util.Optional;

import javax.swing.text.html.Option;

import org.springframework.stereotype.Component;

import com.rmf.apiserverjava.entity.tasks.TaskState;
import com.rmf.apiserverjava.rmfapi.tasks.TaskEventLogApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskLogApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskStateApi;
import com.rmf.apiserverjava.rxjava.eventbus.TaskEvents;

import io.reactivex.rxjava3.core.Observable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * TaskConsumers.
 *
 * <p>
 *     Task 이벤트를 구독하는 이벤트 컨슈머. shared()를 통해 다수의 구독자가 동일한 이벤트를 구독할 수 있다.
 * </p>
 */
@Component
@Getter
@Slf4j
public class TaskConsumers {
	private final TaskEvents taskEvents;

	private final Observable<TaskStateApi> taskStatesConsumer;

	private final Observable<TaskEventLogApi> taskEventLogConsumer;

	public TaskConsumers(TaskEvents taskEvents) {
		this.taskEvents = taskEvents;
		taskStatesConsumer = taskEvents.getTaskStatesEvent().share();
		taskStatesConsumer.doOnNext(data -> log.info("Task State Data received: " + data));
		taskEventLogConsumer = taskEvents.getTaskEventLogsEvent().share();
		taskEventLogConsumer.doOnNext(data -> log.info("Task Event Log Data received: " + data));
	}
}
