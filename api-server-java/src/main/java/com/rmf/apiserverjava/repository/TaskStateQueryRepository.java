package com.rmf.apiserverjava.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.rmf.apiserverjava.dto.page.PaginationDto;
import com.rmf.apiserverjava.dto.tasks.TaskStatesQueryDto;
import com.rmf.apiserverjava.entity.tasks.TaskState;

/*
* 동적쿼리를 사용하여 TaskState 목록을 조회하는 custom Repository.
* */
@Repository
public interface TaskStateQueryRepository {

	/*
	* TaskStatesQueryDto를 기준으로 TaskState 목록을 조회한다.
	* */
	public List<TaskState> findAllTaskStateByQuery(TaskStatesQueryDto taskStatesQueryDto, PaginationDto paginationDto);
}
