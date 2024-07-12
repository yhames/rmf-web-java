package com.rmf.apiserverjava.rosmsgs.ingestor;

import java.util.Queue;

import com.rmf.apiserverjava.rosmsgs.builtin.TimeMsg;

public class IngestorStateMsg {
	// time: Time = Time()  # builtin_interfaces/Time
	// guid: str = ""  # string
	// mode: pydantic.conint(ge=-2147483648, le=2147483647) = 0  # int32
	// request_guid_queue: List[str] = []  # string
	// seconds_remaining: float = 0  # float32

	private TimeMsg time;

	private String guid;

	// int32 (ge=-2147483648, le=2147483647)
	private int mode;

	private Queue<String> requestGuidQueue;

	// float32
	private float secondsRemaining;
}
