package com.rmf.apiserverjava.repository.impl;

import static com.rmf.apiserverjava.entity.scheduledtasks.QScheduledTask.*;
import static com.rmf.apiserverjava.entity.scheduledtasks.QScheduledTaskSchedule.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rmf.apiserverjava.dto.scheduledtasks.ScheduledTaskSearchCondition;
import com.rmf.apiserverjava.entity.scheduledtasks.ScheduledTask;
import com.rmf.apiserverjava.repository.ScheduledTaskRepositoryCustom;

import jakarta.persistence.EntityManager;

@Repository
public class ScheduledTaskRepositoryCustomImpl implements ScheduledTaskRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public ScheduledTaskRepositoryCustomImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public List<ScheduledTask> searchStartBeforeUntilAfter(ScheduledTaskSearchCondition condition) {
		return queryFactory.select(scheduledTask)
			.distinct()
			.from(scheduledTask)
			.leftJoin(scheduledTask.schedules, scheduledTaskSchedule)
			.fetchJoin()
			.where(schedulesStartFromLessThanEqual(condition.getStartBefore()),
				schedulesUntilGreaterThanEqual(condition.getUntilAfter()))
			.orderBy(createOrderBy(condition.getOrderBy()))
			.limit(condition.getLimit())
			.offset(condition.getOffset())
			.fetch();
	}

	private OrderSpecifier<?>[] createOrderBy(String orderBy) {
		if (orderBy == null) {
			return new OrderSpecifier[0];
		}

		List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
		for (String order : orderBy.split(",")) {
			if (order.equals("id")) {
				orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, scheduledTask.id));
			} else if (order.equals("-id")) {
				orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, scheduledTask.id));
			} else if (order.equals("created_by")) {
				orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, scheduledTask.createdBy));
			} else if (order.equals("-created_by")) {
				orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, scheduledTask.createdBy));
			}
		}
		return orderSpecifiers.toArray(new OrderSpecifier[0]);
	}

	private BooleanExpression schedulesStartFromLessThanEqual(LocalDateTime startBefore) {
		return scheduledTaskSchedule.startFrom.isNull()
			.or(scheduledTaskSchedule.startFrom.loe(Timestamp.valueOf(startBefore)));
	}

	private BooleanExpression schedulesUntilGreaterThanEqual(LocalDateTime untilAfter) {
		return scheduledTaskSchedule.until.isNull()
			.or(scheduledTaskSchedule.until.goe(Timestamp.valueOf(untilAfter)));
	}
}
