package com.rmf.apiserverjava.rxjava.eventbus;

import org.springframework.stereotype.Component;

import com.rmf.apiserverjava.entity.tasks.TaskState;
import com.rmf.apiserverjava.rmfapi.tasks.TaskEventLogApi;
import com.rmf.apiserverjava.rmfapi.tasks.TaskStateApi;

import io.reactivex.rxjava3.subjects.PublishSubject;
import lombok.Getter;

/**
 * TaskEvents.
 *
 * <p>
 *   Task 이벤트를 발행하는 이벤트 버스
 * </p>
 */
@Component
@Getter
public class TaskEvents {
	private final PublishSubject<TaskStateApi> taskStatesEvent;
	private final PublishSubject<TaskEventLogApi> taskEventLogsEvent;

	public TaskEvents() {
		taskStatesEvent = PublishSubject.create();
		taskEventLogsEvent = PublishSubject.create();
	}
}
