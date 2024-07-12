package com.rmf.apiserverjava.security.userdetails;

import java.util.ArrayList;
import java.util.Collection;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.rmf.apiserverjava.entity.users.User;
import com.rmf.apiserverjava.security.SecurityRole;

/**
 * CustomUserDetails.
 *
 * <p>
 *     Spring Security 사용자 정보를 담는 클래스
 * </p>
 */
public class CustomUserDetails implements UserDetails {

	private final String username;
	private final String password;
	private final String role;

	public CustomUserDetails(String username, String password, String role) {
		this.username = username;
		this.password = password;
		this.role = role;
	}

	/**
	 * 사용자의 권한을 반환
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add((GrantedAuthority)() -> role);
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Mapper
	public interface MapStruct {

		MapStruct INSTANCE = Mappers.getMapper(MapStruct.class);

		@Mapping(source = "isAdmin", target = "role", qualifiedByName = "isAdminToRole")
		CustomUserDetails toDto(User user);

		@Named("isAdminToRole")
		default String isAdminToRole(Boolean isAdmin) {
			if (isAdmin) {
				return SecurityRole.ROLE_ADMIN.name();
			} else {
				return SecurityRole.ROLE_USER.name();
			}
		}
	}
}
