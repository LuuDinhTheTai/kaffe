package com.me.kaffe.service;

import com.me.kaffe.dto.request.LoginRequest;
import com.me.kaffe.dto.request.RegisterRequest;
import com.me.kaffe.dto.response.AuthResponse;
import com.me.kaffe.entity.Account;
import com.me.kaffe.entity.Role;
import com.me.kaffe.repository.AccountRepository;
import com.me.kaffe.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (accountRepository.findByUsername(request.getUsername()).isPresent()) {
            return AuthResponse.failure("USERNAME_EXISTS");
        }

        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setFullName(request.getFullName());
        account.setNumber(request.getNumber());
        account.setRole(Role.CUSTOMER);
        accountRepository.save(account);

        return AuthResponse.success(jwtUtil.generateToken(request.getUsername()));
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            return AuthResponse.success(jwtUtil.generateToken(request.getUsername()));
        } catch (BadCredentialsException e) {
            return AuthResponse.failure("BAD_CREDENTIALS");
        }
    }
}
