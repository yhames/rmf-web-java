package com.rmf.apiserverjava.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rmf.apiserverjava.entity.jwt.RefreshToken;
import com.rmf.apiserverjava.entity.users.User;

/**
 * RefreshTokenRepository.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByUser(User user);

	void deleteByUser(User user);
}
