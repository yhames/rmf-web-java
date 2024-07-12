package com.rmf.apiserverjava.entity.lifts;

import com.rmf.apiserverjava.entity.health.Health;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lifthealth")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LiftHealth extends Health {
}
