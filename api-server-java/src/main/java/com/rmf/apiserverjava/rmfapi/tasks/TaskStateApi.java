package com.rmf.apiserverjava.rmfapi.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskStateApi {
	private Booking booking;
	private Category category;
	private Detail detail;
	@JsonProperty("unix_millis_start_time")
	private Long unixMillisStartTime;
	@JsonProperty("unix_millis_finish_time")
	private Long unixMillisFinishTime;
	@JsonProperty("original_estimate_millis")
	private EstimateMillis originalEstimateMillis;
	@JsonProperty("estimate_millis")
	private EstimateMillis estimateMillis;
	@JsonProperty("assigned_to")
	private AssignedTo assignedTo;
	private Status status;
	private Dispatch dispatch;
	private Map<String, Phase> phases = new ConcurrentHashMap<>();
	private List<Id> completed;
	private Id active;
	private List<Id> pending;
	private Map<String, Interruption> interruptions;
	private Cancellation cancellation;
	private Killed killed;

	@Builder
	public TaskStateApi(Booking booking, Category category, Detail detail, Long unixMillisStartTime,
		Long unixMillisFinishTime, EstimateMillis originalEstimateMillis, EstimateMillis estimateMillis,
		AssignedTo assignedTo, Status status, Dispatch dispatch, Map<String, Phase> phases,
		List<Id> completed, Id active, List<Id> pending, Map<String, Interruption> interruptions,
		Cancellation cancellation, Killed killed) {
		this.booking = booking;
		this.category = category;
		this.detail = detail;
		this.unixMillisStartTime = unixMillisStartTime;
		this.unixMillisFinishTime = unixMillisFinishTime;
		this.originalEstimateMillis = originalEstimateMillis;
		this.estimateMillis = estimateMillis;
		this.assignedTo = assignedTo;
		this.status = status;
		this.dispatch = dispatch;
		this.completed = completed;
		this.active = active;
		this.pending = pending;
		this.interruptions = interruptions;
		this.cancellation = cancellation;
		this.killed = killed;
		if (phases != null) {
			this.phases = phases;
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class AssignedTo {
		private String group;
		private String name;

		@Builder
		public AssignedTo(String group, String name) {
			this.group = group;
			this.name = name;
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Cancellation {
		@JsonProperty("unix_millis_request_time")
		private Long unixMillisRequestTime;
		private List<String> labels;
	}

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class Killed {
		@JsonProperty("unix_millis_request_time")
		private Long unixMillisRequestTime;
		private List<String> labels;
	}

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Booking {
		private String id;
		@JsonProperty("unix_millis_earliest_start_time")
		private Long unixMillisEarliestStartTime;
		@JsonProperty("unix_millis_request_time")
		private Long unixMillisRequestTime;
		//타입이 Map, String 중 하나임.
		private Object priority;
		private List<String> labels;
		private String requester;

		public void setPriority(Object priority) {
			if (priority instanceof Map || priority instanceof String) {
				this.priority = priority;
			}
		}

		public Booking(String id, Long unixMillisEarliestStartTime, Long unixMillisRequestTime,
			Object priority, List<String> labels, String requester) {
			this.id = id;
			this.unixMillisEarliestStartTime = unixMillisEarliestStartTime;
			this.unixMillisRequestTime = unixMillisRequestTime;
			if (priority instanceof Map || priority instanceof String) {
				this.priority = priority;
			}
			this.labels = labels;
			this.requester = requester;
		}

	}

	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class Category {
		private String category;

		@JsonValue
		public String getCategory() {
			return category;
		}
	}

	@Setter
	@AllArgsConstructor
	public static class Detail {
		//타입이 Map<str, Any>, List, String중에 하나임.
		private Map<String, JsonNode> unknownMap = null;
		private List<JsonNode> unknownList = null;
		private String unknownString = null;

		@JsonCreator
		public Detail(JsonNode node) {
			if (node.isObject()) {
				unknownMap = new HashMap<>();
				node.fields().forEachRemaining(entry -> unknownMap.put(entry.getKey(), entry.getValue()));
			} else if (node.isArray()) {
				unknownList = new ArrayList<>();
				node.elements().forEachRemaining(unknownList::add);
			} else if (node.isTextual()) {
				unknownString = node.asText();
			}
		}

		@JsonValue
		public JsonNode toJsonNode() {
			if (unknownString != null) {
				return new TextNode(unknownString);
			} else if (unknownMap != null) {
				return new ObjectNode(JsonNodeFactory.instance, unknownMap);
			} else if (unknownList != null) {
				return new ArrayNode(JsonNodeFactory.instance, unknownList);
			}
			return NullNode.instance;
		}
	}

	@Getter
	@RequiredArgsConstructor
	public enum Status1 {
		queued("queued"),
		selected("selected"),
		dispatched("dispatched"),
		failed_to_assign("failed_to_assign"),
		canceled_in_flight("canceled_in_flight");

		private final String value;
	}

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class Assignment {
		@JsonProperty("fleet_name")
		private String fleetName;
		@JsonProperty("expected_robot_name")
		private String expectedRobotName;
	}

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class Dispatch {
		private Status1 status;
		private Assignment assignment;
		private List<Error> errors;
	}

	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class EstimateMillis {
		@JsonProperty("estimate_millis")
		private long estimateMillis;

		@JsonValue
		public long getEstimateMillis() {
			return estimateMillis;
		}
	}

	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class Id {
		private long id;

		@JsonValue
		public long getId() {
			return id;
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class ResumedBy {
		@JsonProperty("unix_millis_request_time")
		private Long unixMillisRequestTime;
		private List<String> labels;
	}

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class Interruption {
		@JsonProperty("unix_millis_request_time")
		private Long unixMillisRequestTime;
		private List<String> labels;
		@JsonProperty("resumed_by")
		private ResumedBy resumedBy;
	}

	@Getter
	@RequiredArgsConstructor
	public enum Status {
		uninitialized("uninitialized"),
		blocked("blocked"),
		error("error"),
		failed("failed"),
		queued("queued"),
		standby("standby"),
		underway("underway"),
		delayed("delayed"),
		skipped("skipped"),
		canceled("canceled"),
		killed("killed"),
		completed("completed");

		private final String value;
	}

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class EventState {
		private Id id;
		private Status status;
		private String name;
		private Detail detail;
		private List<Long> deps;
	}

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class Undo {
		@JsonProperty("unix_millis_request_time")
		private Long unixMillisRequestTime;
		private List<String> labels;
	}

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class SkipPhaseRequest {
		@JsonProperty("unix_millis_request_time")
		private Long unixMillisRequestTime;
		private List<String> labels;
		private Undo undo;
	}

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class Phase {
		private Id id;
		private Category category;
		private Detail detail;
		@JsonProperty("unix_millis_start_time")
		private Long unixMillisStartTime;
		@JsonProperty("unix_millis_finish_time")
		private Long unixMillisFinishTime;
		@JsonProperty("original_estimate_millis")
		private EstimateMillis originalEstimateMillis;
		@JsonProperty("estimate_millis")
		private EstimateMillis estimateMillis;
		@JsonProperty("final_event_id")
		private Id finalEventId;
		private Map<String, EventState> events;
		@JsonProperty("skip_requests")
		private List<SkipPhaseRequest> skipRequests;

		@Builder
		public Phase(Id id, Category category) {
			this.id = id;
			this.category = category;
		}
	}
}
