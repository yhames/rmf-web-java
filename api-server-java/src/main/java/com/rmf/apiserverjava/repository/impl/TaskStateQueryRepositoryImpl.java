package com.rmf.apiserverjava.repository.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rmf.apiserverjava.dto.page.PaginationDto;
import com.rmf.apiserverjava.dto.tasks.TaskStatesQueryDto;
import com.rmf.apiserverjava.dto.tasks.TaskStatesQueryRequestDto;
import com.rmf.apiserverjava.entity.tasks.QTaskState;
import com.rmf.apiserverjava.entity.tasks.TaskState;
import com.rmf.apiserverjava.repository.TaskStateQueryRepository;

import jakarta.persistence.EntityManager;

@Repository
@Transactional
public class TaskStateQueryRepositoryImpl implements TaskStateQueryRepository {

	private final JPAQueryFactory qf;

	public TaskStateQueryRepositoryImpl(EntityManager em) {
		this.qf = new JPAQueryFactory(em);
	}

	QTaskState qTaskState = QTaskState.taskState;

	/*
	* QueryDSL을 사용하여 taskStatesQueryDto에서 주어진 조건에 따라 TaskState 목록을 조회한다.
	* */
	@Override
	public List<TaskState> findAllTaskStateByQuery(TaskStatesQueryDto taskStatesQueryDto, PaginationDto paginationDto) {
		BooleanBuilder condition = new BooleanBuilder();
		if (taskStatesQueryDto.getId() != null) {
			condition.and(qTaskState.id.in(taskStatesQueryDto.getId()));
		}

		if (taskStatesQueryDto.getCategory() != null) {
			condition.and(qTaskState.category.in(taskStatesQueryDto.getCategory()));
		}

		if (taskStatesQueryDto.getAssignedTo() != null) {
			condition.and(qTaskState.assignedTo.in(taskStatesQueryDto.getAssignedTo()));
		}

		if (taskStatesQueryDto.getStartTimeBetween() != null) {
			Timestamp start = new Timestamp(taskStatesQueryDto.getStartTimeBetween().getStartTimeMillis());
			Timestamp end = new Timestamp(taskStatesQueryDto.getStartTimeBetween().getEndTimeMillis());
			condition.and(qTaskState.unixMillisStartTime.goe(start));
			condition.and(qTaskState.unixMillisStartTime.loe(end));
		}

		if (taskStatesQueryDto.getFinishTimeBetween() != null) {
			Timestamp start = new Timestamp(taskStatesQueryDto.getFinishTimeBetween().getStartTimeMillis());
			Timestamp end = new Timestamp(taskStatesQueryDto.getFinishTimeBetween().getEndTimeMillis());
			condition.and(qTaskState.unixMillisFinishTime.goe(start));
			condition.and(qTaskState.unixMillisFinishTime.loe(end));
		}

		if (taskStatesQueryDto.getStatus() != null) {
			condition.and(qTaskState.status.in(taskStatesQueryDto.getStatus()));
		}

		JPAQuery<TaskState> query = qf.selectFrom(qTaskState).where(condition);

		if (paginationDto.getLimit() != null && paginationDto.getLimit() > 0) {
			query.limit(paginationDto.getLimit());
		}
		if (paginationDto.getOffset() != null && paginationDto.getOffset() >= 0) {
			query.offset(paginationDto.getOffset());
		}
		if (paginationDto.getOrderBy() != null) {
			List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
			for (Map.Entry<String, Boolean> entry : paginationDto.getOrderBy().entrySet()) {
				String key = entry.getKey();
				boolean asc = entry.getValue();
				OrderSpecifier<?> orderSpecifier;
				if (key.equals("unix_millis_start_time")) {
					orderSpecifier = new OrderSpecifier<>(asc ? Order.ASC : Order.DESC, qTaskState.unixMillisStartTime);
				} else if (key.equals("unix_millis_finish_time")) {
					orderSpecifier
						= new OrderSpecifier<>(asc ? Order.ASC : Order.DESC, qTaskState.unixMillisFinishTime);
				} else if (key.equals("status")) {
					orderSpecifier = new OrderSpecifier<>(asc ? Order.ASC : Order.DESC, qTaskState.status);
				} else if (key.equals("category")) {
					orderSpecifier = new OrderSpecifier<>(asc ? Order.ASC : Order.DESC, qTaskState.category);
				} else if (key.equals("assigned_to")) {
					orderSpecifier = new OrderSpecifier<>(asc ? Order.ASC : Order.DESC, qTaskState.assignedTo);
				} else if (key.equals("task_id")) {
					orderSpecifier = new OrderSpecifier<>(asc ? Order.ASC : Order.DESC, qTaskState.id);
				} else {
					continue;
				}
				orderSpecifiers.add(orderSpecifier);
			}
			query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]));
		}
		List<TaskState> taskStateList = query.fetch();
		return taskStateList;
	}
}
