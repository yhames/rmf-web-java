package com.rmf.apiserverjava.rosmsgs.buildingmap;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParamMsg {
	// name: str = ""  # string
	// type: pydantic.conint(ge=0, le=4294967295) = 0  # uint32
	// value_int: pydantic.conint(ge=-2147483648, le=2147483647) = 0  # int32
	// value_float: float = 0  # float32
	// value_string: str = ""  # string
	// value_bool: bool = False  # bool

	@JsonProperty("name")
	private String name;

	// uint32 (ge=0, le=4294967295)
	@JsonProperty("type")
	private long type;

	// int32 (ge=-2147483648, le=2147483647)
	@JsonProperty("value_int")
	private int valueInt;

	// float32
	@JsonProperty("value_float")
	private float valueFloat;

	@JsonProperty("value_string")
	private String valueString;

	@JsonProperty("value_bool")
	private boolean valueBool;
}
