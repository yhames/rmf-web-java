package com.rmf.apiserverjava.entity.dispensers;

import com.rmf.apiserverjava.baseentity.JsonMixin;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dispenserstate")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DispenserState extends JsonMixin {
}
