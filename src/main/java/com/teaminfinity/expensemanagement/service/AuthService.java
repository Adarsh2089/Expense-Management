package com.teaminfinity.expensemanagement.service;

import com.teaminfinity.expensemanagement.entity.AppUser;
import com.teaminfinity.expensemanagement.entity.Company;
import com.teaminfinity.expensemanagement.enums.Role;
import com.teaminfinity.expensemanagement.repository.CompanyRepository;
import com.teaminfinity.expensemanagement.repository.UserRepository;
import com.teaminfinity.expensemanagement.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Service for handling authentication operations.
 * Includes signup with auto-creation of company and admin user.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CountryCurrencyService countryCurrencyService;
    
    /**
     * Register a new user (first signup creates company and admin user).
     * 
     * @param email User email
     * @param password User password
     * @param fullName User full name
     * @param companyName Company name
     * @param country Company country
     * @return JWT token
     */
    @Transactional
    public String signup(String email, String password, String fullName, String companyName, String country) {
        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User with this email already exists");
        }
        
        // Check if company exists, if not create it
        Company company = companyRepository.findByName(companyName)
                .orElseGet(() -> {
                    Company newCompany = new Company();
                    newCompany.setName(companyName);
                    newCompany.setCountry(country);
                    
                    // Get default currency for country
                    String currency = countryCurrencyService.getDefaultCurrencyForCountry(country);
                    newCompany.setDefaultCurrency(currency);
                    newCompany.setIsManagerApprover(false);
                    
                    return companyRepository.save(newCompany);
                });
        
        // Create user (first user for company is Admin, others are Employee by default)
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setCompany(company);
        
        // First user of company is Admin
        long userCount = userRepository.findByCompanyId(company.getId()).size();
        user.setRole(userCount == 0 ? Role.ADMIN : Role.EMPLOYEE);
        
        userRepository.save(user);
        
        // Generate and return JWT token
        return jwtUtil.generateToken(user);
    }
    
    /**
     * Authenticate user and generate JWT token.
     * 
     * @param email User email
     * @param password User password
     * @return JWT token
     */
    public String login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        
        AppUser user = (AppUser) authentication.getPrincipal();
        return jwtUtil.generateToken(user);
    }
}
