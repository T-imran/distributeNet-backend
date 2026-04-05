package com.distribnet.auth.service;

import com.distribnet.common.dto.*;
import com.distribnet.common.exception.BadRequestException;
import com.distribnet.common.exception.ResourceNotFoundException;
import com.distribnet.common.model.Tenant;
import com.distribnet.common.model.User;
import com.distribnet.common.model.UserSession;
import com.distribnet.common.model.User.Role;
import com.distribnet.common.repository.TenantRepository;
import com.distribnet.common.repository.UserSessionRepository;
import com.distribnet.common.repository.UserRepository;
import com.distribnet.common.security.CustomUserDetails;
import com.distribnet.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthSessionDto login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new BadRequestException("Invalid email or password");
        }

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findByIdAndTenantId(java.util.UUID.fromString(principal.getId()), java.util.UUID.fromString(principal.getTenantId()))
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = tokenProvider.generateToken(authentication);

        UserSession session = new UserSession();
        session.setId(UUID.randomUUID());
        session.setTenant(user.getTenant());
        session.setUser(user);
        session.setExpiresAt(LocalDateTime.now().plusDays(7));
        String refreshToken = tokenProvider.generateRefreshToken(authentication, session.getId());
        session.setRefreshTokenHash(hashToken(refreshToken));
        userSessionRepository.save(session);

        return buildSession(accessToken, refreshToken, user);
    }

    @Transactional
    public User register(RegisterRequest request) {
        String normalizedDomain = request.getTenantDomain().toLowerCase(Locale.ROOT);
        String normalizedEmail = request.getEmail().toLowerCase(Locale.ROOT);

        Tenant tenant = tenantRepository.findByDomain(request.getTenantDomain().toLowerCase(Locale.ROOT))
                .orElseGet(() -> {
                    Tenant created = new Tenant();
                    created.setName(request.getTenantDomain());
                    created.setDomain(normalizedDomain);
                    created.setStatus(Tenant.TenantStatus.ACTIVE);
                    return tenantRepository.save(created);
                });

        userRepository.findByEmail(normalizedEmail).ifPresent(existing -> {
            throw new BadRequestException("A user with this email already exists");
        });

        User user = new User();
        user.setTenant(tenant);
        user.setName(request.getName());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.valueOf(request.getRole().toUpperCase(Locale.ROOT)));
        user.setStatus(User.UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AuthSessionDto me(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return buildSession(null, null, user);
    }

    @Transactional(readOnly = true)
    public AuthSessionDto refresh(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException("Refresh token is invalid or expired");
        }
        if (!"refresh".equals(tokenProvider.getTokenType(refreshToken))) {
            throw new BadRequestException("Invalid token type for refresh");
        }

        UUID sessionId = tokenProvider.getSessionId(refreshToken);
        if (sessionId == null) {
            throw new BadRequestException("Refresh token session is missing");
        }

        UserSession session = userSessionRepository.findByIdAndRevokedAtIsNullAndExpiresAtAfter(sessionId, LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("Refresh session is invalid or expired"));
        if (!hashToken(refreshToken).equals(session.getRefreshTokenHash())) {
            throw new BadRequestException("Refresh token does not match active session");
        }

        User user = session.getUser();
        if (!user.getEmail().equalsIgnoreCase(tokenProvider.getUsernameFromToken(refreshToken))) {
            throw new BadRequestException("Refresh token user mismatch");
        }

        CustomUserDetails userDetails = CustomUserDetails.fromUser(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String newAccessToken = tokenProvider.generateToken(authentication);
        String newRefreshToken = tokenProvider.generateRefreshToken(authentication, session.getId());
        session.setRefreshTokenHash(hashToken(newRefreshToken));
        session.setExpiresAt(LocalDateTime.now().plusDays(7));
        userSessionRepository.save(session);

        return buildSession(newAccessToken, newRefreshToken, user);
    }

    @Transactional
    public void logout(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken) || !"refresh".equals(tokenProvider.getTokenType(refreshToken))) {
            throw new BadRequestException("Refresh token is invalid or expired");
        }
        UUID sessionId = tokenProvider.getSessionId(refreshToken);
        if (sessionId == null) {
            throw new BadRequestException("Refresh token session is missing");
        }

        UserSession activeSession = userSessionRepository.findByIdAndRevokedAtIsNull(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        if (!hashToken(refreshToken).equals(activeSession.getRefreshTokenHash())) {
            throw new BadRequestException("Refresh token does not match active session");
        }
        activeSession.setRevokedAt(LocalDateTime.now());
        userSessionRepository.save(activeSession);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Unable to hash token", ex);
        }
    }

    private AuthSessionDto buildSession(String accessToken, String refreshToken, User user) {
        return AuthSessionDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(AuthUserDto.builder()
                        .id(user.getId().toString())
                        .tenantId(user.getTenant().getId().toString())
                        .name(user.getName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .role(user.getRole().name())
                        .status(user.getStatus().name().toLowerCase(Locale.ROOT))
                        .region(user.getRegion())
                        .lastLoginAt(user.getLastLoginAt())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .build())
                .tenant(AuthTenantDto.builder()
                        .id(user.getTenant().getId().toString())
                        .name(user.getTenant().getName())
                        .domain(user.getTenant().getDomain())
                        .plan(user.getTenant().getPlan())
                        .primaryColor(user.getTenant().getPrimaryColor())
                        .appName(user.getTenant().getAppName())
                        .build())
                .build();
    }
}
