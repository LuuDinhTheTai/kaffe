package com.me.kaffe.service;

import com.me.kaffe.dto.request.LoginRequest;
import com.me.kaffe.dto.request.RegisterRequest;
import com.me.kaffe.dto.response.AuthResponse;
import com.me.kaffe.entity.Account;
import com.me.kaffe.entity.Role;
import com.me.kaffe.repository.AccountRepository;
import com.me.kaffe.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthService - Authentication Service Layer
 * Handles business logic for user registration and login
 * 
 * Flow: Repository -> Service -> Controller
 * - Repository: Data access to Account entities
 * - Service: Business logic, password encoding, JWT generation
 * - Controller: HTTP handling, DTOs mapping
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Register a new user account
     * 
     * @param request RegisterRequest DTO with validation
     * @return AuthResponse with JWT token or error code
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register new user with username: {}", request.getUsername());

        // Check if username already exists
        if (accountRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Registration failed: Username already exists - {}", request.getUsername());
            return AuthResponse.failure("USERNAME_EXISTS");
        }

        try {
            // Create new account entity
            Account account = new Account();
            account.setUsername(request.getUsername());
            account.setPassword(passwordEncoder.encode(request.getPassword()));
            account.setFullName(request.getFullName());
            account.setNumber(request.getNumber());
            account.setRole(Role.CUSTOMER);

            // Persist account to database
            Account savedAccount = accountRepository.save(account);
            log.info("User successfully registered: {} (ID: {})", request.getUsername(), savedAccount.getUniqueId());

            // Generate JWT token
            String token = jwtUtil.generateToken(request.getUsername());
            return AuthResponse.success(token);

        } catch (Exception e) {
            log.error("Error during registration for user: {}", request.getUsername(), e);
            return AuthResponse.failure("REGISTRATION_ERROR");
        }
    }

    /**
     * Authenticate user and generate JWT token
     * 
     * @param request LoginRequest DTO with validation
     * @return AuthResponse with JWT token or error code
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting to login user: {}", request.getUsername());

        try {
            // Authenticate credentials using Spring Security AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            log.info("User successfully authenticated: {}", request.getUsername());

            // Generate JWT token on successful authentication
            String token = jwtUtil.generateToken(request.getUsername());
            return AuthResponse.success(token);

        } catch (BadCredentialsException e) {
            log.warn("Login failed: Bad credentials for user - {}", request.getUsername());
            return AuthResponse.failure("BAD_CREDENTIALS");
        } catch (Exception e) {
            log.error("Error during login for user: {}", request.getUsername(), e);
            return AuthResponse.failure("LOGIN_ERROR");
        }
    }
}
