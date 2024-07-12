package com.rmf.apiserverjava.entity.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
 * Email entity.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "email", indexes = {
	@Index(name = "FK_EMAIL_VALIDATION_CODE_ID", columnList = "validation_code_id")},
	uniqueConstraints = {
	@UniqueConstraint(name = "UIDX_EMAIL_VALIDATION_CODE_ID_00", columnNames = "validation_code_id"),
	@UniqueConstraint(name = "UIDX_EMAIL_EMAIL_00", columnNames = "email")
})
public class Email {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, unique = true, columnDefinition = "INT")
	private int id;

	@Column(nullable = false, unique = true, columnDefinition = "VARCHAR(255)")
	private String email;

	@Column(nullable = false, columnDefinition = "BOOLEAN")
	private boolean isVerified;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "validation_code_id", unique = true, nullable = false, columnDefinition = "INT")
	private ValidationCode validationCode;

	// public void setVerified(boolean isVerified) {
	// 	this.isVerified = isVerified;
	// }

	@Builder
	public Email(String email, boolean isVerified, ValidationCode validationCode) {
		this.email = email;
		this.isVerified = isVerified;
		this.validationCode = validationCode;
	}
}
