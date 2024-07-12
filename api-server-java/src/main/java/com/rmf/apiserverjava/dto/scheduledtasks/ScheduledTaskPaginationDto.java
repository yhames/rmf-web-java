package com.rmf.apiserverjava.dto.scheduledtasks;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduledTaskPaginationDto {

	@NotEmpty
	@NotNull
	private String start_before;

	@NotEmpty
	@NotNull
	private String until_after;

	@Max(1000)
	private int limit;

	@Min(0)
	private int offset;

	private String order_by;

	protected ScheduledTaskPaginationDto() {
		limit = 100;
		offset = 0;
	}

	@Builder
	public ScheduledTaskPaginationDto(Integer limit, Integer offset, String order_by,
		String start_before, String until_after) {
		this.limit = limit == null ? 100 : limit;
		this.offset = offset == null ? 0 : offset;
		this.order_by = order_by;
		this.start_before = start_before;
		this.until_after = until_after;
	}
}
