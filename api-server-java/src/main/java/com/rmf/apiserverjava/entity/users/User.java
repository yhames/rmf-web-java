package com.rmf.apiserverjava.entity.users;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * User entity.
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"user\"", indexes = {
	@Index(name = "FK_USER_EMAIL_ID", columnList = "email_id")},
	uniqueConstraints = {
	@UniqueConstraint(name = "UIDX_USER_EMAIL_ID_00", columnNames = "email_id"),
})
public class User {

	@Id
	@Column(nullable = false, unique = true, columnDefinition = "VARCHAR(255)")
	private String username;

	@Column(nullable = false, columnDefinition = "VARCHAR(255)")
	private String password;

	@Column(name = "is_admin", nullable = false, columnDefinition = "BOOLEAN")
	private Boolean isAdmin;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "email_id", unique = true, nullable = false, columnDefinition = "INT")
	private Email email;

	public void setPassword(String password) {
		this.password = password;
	}

	@Builder
	public User(String username, String password, Boolean isAdmin, Email email) {
		this.username = username;
		this.password = password;
		this.isAdmin = isAdmin;
		this.email = email;
	}
}
