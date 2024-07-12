package com.rmf.apiserverjava.entity.ingestors;

import com.rmf.apiserverjava.entity.health.Health;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ingestorhealth")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IngestorHealth extends Health {
}
