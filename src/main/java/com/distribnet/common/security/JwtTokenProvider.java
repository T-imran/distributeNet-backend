package com.distribnet.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();
        return buildToken(userPrincipal, jwtExpirationMs, "access", null);
    }

    public String generateRefreshToken(Authentication authentication, UUID sessionId) {
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();
        return buildToken(userPrincipal, refreshExpirationMs, "refresh", sessionId);
    }

    private String buildToken(CustomUserDetails userPrincipal, long expirationMs, String tokenType, UUID sessionId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        var builder = Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("role", userPrincipal.getRole())
                .claim("tenantId", userPrincipal.getTenantId())
                .claim("tokenType", tokenType)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256);
        if (sessionId != null) {
            builder.claim("sessionId", sessionId.toString());
        }
        return builder.compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getTokenType(String token) {
        return getClaims(token).get("tokenType", String.class);
    }

    public UUID getSessionId(String token) {
        String sessionId = getClaims(token).get("sessionId", String.class);
        return sessionId != null ? UUID.fromString(sessionId) : null;
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
