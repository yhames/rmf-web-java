package com.rmf.apiserverjava.global.converter;

import java.util.Map;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.rmf.apiserverjava.global.exception.custom.JsonProcessingException;
import com.rmf.apiserverjava.global.utils.ObjectMapperUtils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JsonConverter
 *
 * <p>
 *     제네릭 Converter를 제공하기 위한 추상 클래스.
 * </p>
 */
@Converter
public abstract class JsonConverter<T> implements AttributeConverter<T, String> {

	private static final String FAILED = "JSON CONVERTER FAILED: ";

	private final TypeReference<T> typeReference; // 2

	public JsonConverter(TypeReference<T> typeReference) {
		this.typeReference = typeReference;
	}

	@Override
	public String convertToDatabaseColumn(T object) {
		try {
			return ObjectMapperUtils.MAPPER.writeValueAsString(object);
		} catch (JacksonException e) {
			throw new JsonProcessingException(FAILED + typeReference);
		}
	}

	@Override
	public T convertToEntityAttribute(String data) {
		try {
			return ObjectMapperUtils.MAPPER.convertValue(data, typeReference);
		} catch (IllegalArgumentException e) {
			throw new JsonProcessingException(FAILED + typeReference);
		}
	}
}
