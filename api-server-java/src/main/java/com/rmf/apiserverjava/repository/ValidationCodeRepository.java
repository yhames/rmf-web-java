package com.rmf.apiserverjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rmf.apiserverjava.entity.users.ValidationCode;

public interface ValidationCodeRepository extends JpaRepository<ValidationCode, Integer> {
}
