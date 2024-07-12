package com.rmf.apiserverjava.entity.users;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * UserRole entity.
 */

@Entity
@Table(name = "user_role", indexes = {
	@Index(name = "FK_USER_ROLE_USER_ID", columnList = "user_id"),
	@Index(name = "FK_USER_ROLE_ROLE_ID", columnList = "role_id")},
	uniqueConstraints = {
	@UniqueConstraint(name = "UIDX_USER_ROLE_USER_ID_ROLE_ID_00", columnNames = {"userId", "roleId"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRole {

	// 주석은 추후 로그인 관련 기능 추가시 추가
	// @Id
	// @GeneratedValue(strategy = GenerationType.IDENTITY)
	// @Column(updatable = false, unique = true, columnDefinition = "INTEGER")
	// private int id;

	//추후 로그인 관련 기능 추가시 @Id삭제
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, columnDefinition = "VARCHAR(255)")
	private User user;

	//추후 로그인 관련 기능 추가시 @Id삭제
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id", nullable = false, columnDefinition = "INT")
	private Role role;
}
