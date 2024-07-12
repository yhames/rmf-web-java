package com.rmf.apiserverjava.entity.lifts;

import com.rmf.apiserverjava.baseentity.JsonMixin;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "liftstate")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LiftState extends JsonMixin {
}
