package com.distribnet.auth.controller;

import com.distribnet.auth.service.AuthService;
import com.distribnet.common.dto.ApiResponse;
import com.distribnet.common.dto.AuthSessionDto;
import com.distribnet.common.dto.LoginRequest;
import com.distribnet.common.dto.LogoutRequest;
import com.distribnet.common.dto.RefreshTokenRequest;
import com.distribnet.common.dto.RegisterRequest;
import com.distribnet.common.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthSessionDto>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request), "Login successful"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<java.util.Map<String, String>>> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(
                java.util.Map.of("id", user.getId().toString(), "email", user.getEmail()),
                "User registered successfully"
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthSessionDto>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.refresh(request.getRefreshToken()), "Token refreshed"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.successMessage("Logout successful"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.badRequest().body(ApiResponse.failure("Authentication required", java.util.List.of()));
        }
        return ResponseEntity.ok(ApiResponse.success(authService.me(authentication.getName()), "Authenticated user fetched"));
    }
}
