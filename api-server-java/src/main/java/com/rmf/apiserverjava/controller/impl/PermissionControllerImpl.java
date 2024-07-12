package com.rmf.apiserverjava.controller.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rmf.apiserverjava.controller.PermissionController;

/**
 * PermissionControllerImpl
 *
 * <p>
 *     PermissionController의 구현체.
 * </p>
 */
@Controller
@RequestMapping("/permissions")
public class PermissionControllerImpl implements PermissionController {

	/**
	 * getPermissions
	 */
	@Override
	@GetMapping
	public ResponseEntity<String> getPermissions() {
		// TODO: Implement this method
		return ResponseEntity.ok().build();
	}
}
