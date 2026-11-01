package com.veloia.smartwater.controller;

import com.veloia.smartwater.dto.auth.LoginRequest;
import com.veloia.smartwater.dto.auth.LoginResponse;
import com.veloia.smartwater.dto.auth.UserDto;
import com.veloia.smartwater.dto.common.ApiResponse;
import com.veloia.smartwater.entity.AppUser;
import com.veloia.smartwater.exception.ResourceNotFoundException;
import com.veloia.smartwater.repository.AppUserRepository;
import com.veloia.smartwater.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication & RBAC", description = "JWT Token issuance, authentication, and role identity management")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AppUserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider,
                          AppUserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    @Transactional
    @Operation(summary = "Authenticate user and issue JWT access token")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            AppUser user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("AppUser", "username", request.getUsername()));

            user.setLastLoginAt(Instant.now());
            userRepository.save(user);

            String token = tokenProvider.generateToken(user);
            UserDto userDto = toUserDto(user);

            LoginResponse response = new LoginResponse(token, tokenProvider.getExpirationMs(), userDto);
            return ResponseEntity.ok(ApiResponse.success("Authentication successful", response));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid username or password"));
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Get currently authenticated user details from JWT")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(401).body(ApiResponse.error("User is not authenticated"));
        }

        String username = auth.getName();
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("AppUser", "username", username));

        return ResponseEntity.ok(ApiResponse.success(toUserDto(user)));
    }

    @GetMapping("/demo-accounts")
    @Operation(summary = "List pre-configured simulated role accounts for rapid demo switching")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getDemoAccounts() {
        List<Map<String, String>> accounts = List.of(
                Map.of("username", "admin", "role", "ADMIN", "description", "Full System Administrator"),
                Map.of("username", "operator", "role", "OPERATOR", "description", "Customer & Meter Billing Operations"),
                Map.of("username", "technician", "role", "TECHNICIAN", "description", "Field Maintenance & Leak Response"),
                Map.of("username", "analyst", "role", "ANALYST", "description", "Hydraulic & Quality Analytics Officer")
        );
        return ResponseEntity.ok(ApiResponse.success("Demo credentials available (default password: Password123!)", accounts));
    }

    private UserDto toUserDto(AppUser user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole());
        dto.setDepartment(user.getDepartment());
        dto.setStatus(user.getStatus());
        dto.setLastLoginAt(user.getLastLoginAt());
        return dto;
    }
}
