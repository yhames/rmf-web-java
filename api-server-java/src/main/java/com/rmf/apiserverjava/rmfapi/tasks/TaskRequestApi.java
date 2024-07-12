package com.rmf.apiserverjava.rmfapi.tasks;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskRequestApi {

	@JsonProperty("unix_millis_earliest_start_time")
	private Long unixMillisEarliestStartTime;

	@JsonProperty("unix_millis_request_time")
	private Long unixMillisRequestTime;

	//TODO: Key값으로 사용되는 String은 "Type"과 "Value"로 구성됨.
	private Map<String, Object> priority;

	private String category;

	private JsonNode description;

	private List<String> labels;

	private String requester;

	@Builder
	public TaskRequestApi(Long unixMillisEarliestStartTime,
							Long unixMillisRequestTime,
							Map<String, Object> priority,
							String category,
							List<String> labels,
							String requester) {
		this.unixMillisEarliestStartTime = unixMillisEarliestStartTime;
		this.unixMillisRequestTime = unixMillisRequestTime;
		this.priority = priority;
		this.category = category;
		this.labels = labels;
		this.requester = requester;
	}
}
