package com.rmf.apiserverjava.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rmf.apiserverjava.dto.page.PaginationDto;
import com.rmf.apiserverjava.entity.users.QUser;
import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.repository.UserQueryRepository;

import jakarta.persistence.EntityManager;

@Repository
@Transactional
public class UserQueryRepositoryImpl implements UserQueryRepository {

	private final JPAQueryFactory qf;

	public UserQueryRepositoryImpl(EntityManager em) {
		this.qf = new JPAQueryFactory(em);
	}

	QUser qUser = QUser.user;

	@Override
	public List<User> findAllUserByQuery(String usernameStart, Boolean isAdmin, PaginationDto paginationDto) {
		JPAQuery<User> query = qf.selectFrom(qUser);

		if (usernameStart != null) {
			query.where(qUser.username.startsWith(usernameStart));
		}
		if (isAdmin != null) {
			if (isAdmin == true) {
				query.where(qUser.isAdmin.eq(true));
			} else {
				query.where(qUser.isAdmin.eq(false));
			}
		}
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
				if (key.equals("id")) {
					orderSpecifier = asc ? qUser.username.asc() : qUser.username.desc();
				} else if (key.equals("email")) {
					orderSpecifier = asc ? qUser.email.email.asc() : qUser.email.email.desc();
				} else {
					continue;
				}
				orderSpecifiers.add(orderSpecifier);
			}
			query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]));
		}
		List<User> userList = query.fetch();
		return userList;
	}
}
