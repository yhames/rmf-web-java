package com.rmf.apiserverjava.entity.jwt;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.rmf.apiserverjava.entity.users.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * RefreshToken Entity.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_token", indexes = {
	@Index(name = "FK_REFRESH_TOKEN_USER_ID", columnList = "user_id")}, uniqueConstraints = {
	@UniqueConstraint(name = "UIDX_REFRESH_TOKEN_USER_ID_00", columnNames = "user_id"),
})
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, unique = true, nullable = false, columnDefinition = "INTEGER")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", unique = true, nullable = false, columnDefinition = "VARCHAR(255)")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;

	@Column(name = "refresh_token", nullable = false, columnDefinition = "VARCHAR(255)")
	private String refreshToken;

	@Column(nullable = false, columnDefinition = "VARCHAR(255)")
	private String ip;

	@Builder
	public RefreshToken(User user, String refreshToken, String ip) {
		this.user = user;
		this.refreshToken = refreshToken;
		this.ip = ip;
	}

	/**
	 * 토큰의 내용과 ip를 수정한다.
	 */
	public void updateToken(String token, String ip) {
		this.refreshToken = token;
		this.ip = ip;
	}
}
