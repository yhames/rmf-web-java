package com.rmf.apiserverjava.controller;

import org.springframework.http.ResponseEntity;

/**
 * PermissionController
 *
 * <p>
 *     Interface for PermissionController.
 * </p>
 */
public interface PermissionController {

	ResponseEntity<String> getPermissions();
}
