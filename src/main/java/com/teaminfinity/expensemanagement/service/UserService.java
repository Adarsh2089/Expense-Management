package com.teaminfinity.expensemanagement.service;

import com.teaminfinity.expensemanagement.entity.AppUser;
import com.teaminfinity.expensemanagement.enums.Role;
import com.teaminfinity.expensemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for user management operations.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Create a new user (Admin only operation).
     * 
     * @param email User email
     * @param password User password
     * @param fullName User full name
     * @param role User role
     * @param companyId Company ID
     * @param managerId Manager ID (optional)
     * @return Created user
     */
    @Transactional
    public AppUser createUser(String email, String password, String fullName, 
                             Role role, Long companyId, Long managerId) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User with this email already exists");
        }
        
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setRole(role);
        
        // Set company (simplified - in production, fetch from repository)
        user.setCompany(userRepository.findById(1L).get().getCompany());
        
        // Set manager if provided
        if (managerId != null) {
            AppUser manager = userRepository.findById(managerId)
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
            user.setManager(manager);
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Get all users for a company.
     */
    public List<AppUser> getUsersByCompanyId(Long companyId) {
        return userRepository.findByCompanyId(companyId);
    }
    
    /**
     * Get users by role.
     */
    public List<AppUser> getUsersByRole(Long companyId, Role role) {
        return userRepository.findByCompanyIdAndRole(companyId, role);
    }
    
    /**
     * Get user by ID.
     */
    public AppUser getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    /**
     * Get user by email.
     */
    public AppUser getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
