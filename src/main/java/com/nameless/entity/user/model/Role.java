package com.nameless.entity.user.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nameless.entity.user.model.Permission.ADMIN_CREATE;
import static com.nameless.entity.user.model.Permission.ADMIN_DELETE;
import static com.nameless.entity.user.model.Permission.ADMIN_READ;
import static com.nameless.entity.user.model.Permission.ADMIN_UPDATE;
import static com.nameless.entity.user.model.Permission.MANAGER_CREATE;
import static com.nameless.entity.user.model.Permission.MANAGER_DELETE;
import static com.nameless.entity.user.model.Permission.MANAGER_READ;
import static com.nameless.entity.user.model.Permission.MANAGER_UPDATE;

@Getter
@RequiredArgsConstructor
public enum Role {

  USER(Collections.emptySet()),


  ADMIN(
          Set.of(
                  ADMIN_READ,
                  ADMIN_UPDATE,
                  ADMIN_DELETE,
                  ADMIN_CREATE,
                  MANAGER_READ,
                  MANAGER_UPDATE,
                  MANAGER_DELETE,
                  MANAGER_CREATE
          )
  ),

  MANAGER(
          Set.of(
                  MANAGER_READ,
                  MANAGER_UPDATE,
                  MANAGER_DELETE,
                  MANAGER_CREATE
          )
  );

  private final Set<Permission> permissions;

  public List<SimpleGrantedAuthority> getAuthorities() {
    // Get the set of permissions associated with the current role
    var authorities = getPermissions()
            .stream() // Create a stream of permissions
            .map(permission -> new SimpleGrantedAuthority(permission.getPermission())) // Convert each permission to a SimpleGrantedAuthority object
            .collect(Collectors.toList()); // Collect the result into a list of SimpleGrantedAuthority objects

    // Add the role itself as a SimpleGrantedAuthority (e.g., "ROLE_ADMIN")
    authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

    // Return the full list of authorities (permissions + role)
    return authorities;
  }
}
