package com.teaminfinity.expensemanagement.controller;

import com.teaminfinity.expensemanagement.dto.auth.AuthResponse;
import com.teaminfinity.expensemanagement.dto.auth.LoginRequest;
import com.teaminfinity.expensemanagement.dto.auth.SignupRequest;
import com.teaminfinity.expensemanagement.entity.AppUser;
import com.teaminfinity.expensemanagement.service.AuthService;
import com.teaminfinity.expensemanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for authentication operations.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final UserService userService;
    
    /**
     * Signup endpoint - creates company and admin user on first signup.
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        String token = authService.signup(
                request.getEmail(),
                request.getPassword(),
                request.getFullName(),
                request.getCompanyName(),
                request.getCountry()
        );
        
        // Get user details
        AppUser user = userService.getUserByEmail(request.getEmail());
        
        AuthResponse response = new AuthResponse(
                token,
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.getId()
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Login endpoint.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        
        // Get user details
        AppUser user = userService.getUserByEmail(request.getEmail());
        
        AuthResponse response = new AuthResponse(
                token,
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.getId()
        );
        
        return ResponseEntity.ok(response);
    }
}
