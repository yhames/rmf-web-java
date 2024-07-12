package com.rmf.apiserverjava.rmfapi.fleet;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rmf.apiserverjava.baseentity.LogMixin;
import com.rmf.apiserverjava.entity.fleets.FleetLogLog;
import com.rmf.apiserverjava.entity.fleets.FleetLogRobotsLog;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogLog;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhasesEventsLog;
import com.rmf.apiserverjava.entity.tasks.TaskEventLogPhasesLog;
import com.rmf.apiserverjava.rmfapi.Tier;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class LogEntryApi {
	// conint ge=0, lt=4294967296
	// Sequence number for this entry.
	// Each entry has a unique sequence number which monotonically increase, until integer overflow causes a wrap around
	private int seq;
	// The importance level of the log entry
	private Tier tier;
	@JsonProperty("unix_millis_time")
	private long unixMillisTime;
	// The text of the log entry
	private String text;

	@Builder
	public LogEntryApi(int seq, Tier tier, long unixMillisTime, String text) {
		this.seq = seq;
		this.tier = tier;
		this.unixMillisTime = unixMillisTime;
		this.text = text;
	}

	public LogEntryApi(LogMixin log) {
		this.seq = log.getSeq();
		this.tier = log.getTier();
		this.unixMillisTime = log.getUnixMillisTime();
		this.text = log.getText();
	}

	@Mapper
	public interface MapStruct {
		MapStruct INSTANCE = Mappers.getMapper(MapStruct.class);

		FleetLogLog toFleetLogLogEntity(LogEntryApi fleetLogLog);

		FleetLogRobotsLog toFleetLogRobotsLogEntity(LogEntryApi fleetLogRobotsLog);

		TaskEventLogLog toTaskEventLogLogEntity(LogEntryApi taskEventLogLog);

		TaskEventLogPhasesLog toTaskEventLogPhasesLogEntity(LogEntryApi phasesLog);

		TaskEventLogPhasesEventsLog toTaskEventLogPhasesEventLogEntity(LogEntryApi eventsLog);
	}
}
