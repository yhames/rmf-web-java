package com.rmf.apiserverjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rmf.apiserverjava.entity.users.User;

/**
 * UserRepository.
 */
public interface UserRepository extends JpaRepository<User, String> {
}
