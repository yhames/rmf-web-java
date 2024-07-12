package com.rmf.apiserverjava.entity.ingestors;

import com.rmf.apiserverjava.baseentity.JsonMixin;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ingestorstate")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IngestorState extends JsonMixin {
}
