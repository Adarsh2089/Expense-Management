package com.teaminfinity.expensemanagement.controller;

import com.teaminfinity.expensemanagement.dto.user.CreateUserRequest;
import com.teaminfinity.expensemanagement.dto.user.UserResponse;
import com.teaminfinity.expensemanagement.entity.AppUser;
import com.teaminfinity.expensemanagement.enums.Role;
import com.teaminfinity.expensemanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for user management operations.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * Create a new user (Admin only).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        AppUser user = userService.createUser(
                request.getEmail(),
                request.getPassword(),
                request.getFullName(),
                request.getRole(),
                request.getCompanyId(),
                request.getManagerId()
        );
        
        return ResponseEntity.ok(mapToUserResponse(user));
    }
    
    /**
     * Get all users in the same company.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<UserResponse>> getUsers(@AuthenticationPrincipal AppUser currentUser) {
        List<AppUser> users = userService.getUsersByCompanyId(currentUser.getCompany().getId());
        
        List<UserResponse> response = users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get users by role.
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable Role role,
                                                             @AuthenticationPrincipal AppUser currentUser) {
        List<AppUser> users = userService.getUsersByRole(currentUser.getCompany().getId(), role);
        
        List<UserResponse> response = users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get current user details.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal AppUser currentUser) {
        return ResponseEntity.ok(mapToUserResponse(currentUser));
    }
    
    private UserResponse mapToUserResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.getCompany().getId(),
                user.getCompany().getName(),
                user.getManager() != null ? user.getManager().getId() : null,
                user.getManager() != null ? user.getManager().getFullName() : null
        );
    }
}
