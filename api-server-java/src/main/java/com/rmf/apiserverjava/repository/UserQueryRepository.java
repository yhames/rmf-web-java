package com.rmf.apiserverjava.repository;

import java.util.List;

import com.rmf.apiserverjava.dto.page.PaginationDto;
import com.rmf.apiserverjava.entity.users.User;

public interface UserQueryRepository {

	List<User> findAllUserByQuery(String usernameStart, Boolean isAdmin, PaginationDto paginationDto);
}
