package com.rmf.apiserverjava.entity.dispensers;

import com.rmf.apiserverjava.entity.health.Health;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dispenserhealth")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DispenserHealth extends Health {
}
