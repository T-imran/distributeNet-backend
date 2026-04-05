package com.distribnet.common.security;

import com.distribnet.common.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final String id;
    private final String email;
    private final String password;
    private final String role;
    private final String status;
    private final String tenantId;
    private final String tenantDomain;
    private final Collection<? extends GrantedAuthority> authorities;

    public static CustomUserDetails fromUser(User user) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        return new CustomUserDetails(
                user.getId().toString(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getTenant().getId().toString(),
                user.getTenant().getDomain(),
                Collections.singletonList(authority)
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !"SUSPENDED".equalsIgnoreCase(status);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
