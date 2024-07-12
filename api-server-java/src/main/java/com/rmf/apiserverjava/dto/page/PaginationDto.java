package com.rmf.apiserverjava.dto.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rmf.apiserverjava.dto.tasks.TaskStatesQueryRequestDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PaginationDto {

	private Integer limit;
	private Integer offset;
	private Map<String, Boolean> orderBy;

	@Builder
	public PaginationDto(Integer limit, Integer offset, String orderBy) {
		this.limit = limit;
		this.offset = offset;
		if (orderBy != null) {
			HashMap<String, Boolean> orderByMap = new HashMap<>();
			String[] split = orderBy.split(",");
			for (String s : split) {
				if (s.startsWith("-")) {
					orderByMap.put(s.substring(1), false);
				} else {
					orderByMap.put(s, true);
				}
			}
			this.orderBy = orderByMap;
		} else {
			this.orderBy = null;
		}
	}
}
