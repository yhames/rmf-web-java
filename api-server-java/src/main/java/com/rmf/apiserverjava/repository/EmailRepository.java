package com.rmf.apiserverjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rmf.apiserverjava.entity.users.Email;

public interface EmailRepository extends JpaRepository<Email, Integer> {

	boolean existsByEmail(String email);

	Email findByEmail(String email);
}
