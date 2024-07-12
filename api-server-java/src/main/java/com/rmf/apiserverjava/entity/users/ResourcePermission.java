package com.rmf.apiserverjava.entity.users;

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
import lombok.NoArgsConstructor;

/**
 * ResourcePermission entity.
 */

@Entity
@Table(name = "resourcepermission", indexes = {
	@Index(name = "FK_RESOURCEPERMISSION_ROLE_ID", columnList = "role_id"),
	@Index(name = "IDX_RESOURCEPERMISSION_AUTHZ_GRP_00", columnList = "IDX_RESOURCEPERMISSION_AUTHZ_GRP_00"),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResourcePermission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, unique = true, columnDefinition = "INT")
	private int id;

	@Column(name = "authz_grp", nullable = false, columnDefinition = "VARCHAR(255)")
	private String authzGrp;

	@Column(nullable = false, columnDefinition = "VARCHAR(255)")
	private String action;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id", nullable = false, columnDefinition = "VARCHAR(255)")
	private Role role;
}
