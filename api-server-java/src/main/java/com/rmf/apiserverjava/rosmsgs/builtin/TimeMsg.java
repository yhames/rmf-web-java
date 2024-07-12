package com.rmf.apiserverjava.rosmsgs.builtin;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeMsg {
	// sec: pydantic.conint(ge=-2147483648, le=2147483647) = 0  # int32
	// nanosec: pydantic.conint(ge=0, le=4294967295) = 0  # uint32

	// int32 (ge=-2147483648, le=2147483647)
	@JsonProperty("sec")
	private int sec;

	// uint32 (ge=0, le=4294967295)
	@JsonProperty("nanosec")
	private long nanoSec;

	@Builder
	public TimeMsg(int sec, long nanoSec) {
		this.sec = sec;
		this.nanoSec = nanoSec;
	}

	public static TimeMsg now() {
		return TimeMsg.builder()
			.sec((int)System.currentTimeMillis())
			.nanoSec(System.nanoTime())
			.build();
	}

	public boolean isEqual(TimeMsg timeMsg) {
		if (this.sec != timeMsg.getSec()) {
			return false;
		}
		return this.nanoSec == timeMsg.getNanoSec();
	}

}
