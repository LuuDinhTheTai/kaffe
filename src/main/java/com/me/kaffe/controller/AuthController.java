package com.me.kaffe.controller;

import com.me.kaffe.dto.request.LoginRequest;
import com.me.kaffe.dto.request.RegisterRequest;
import com.me.kaffe.dto.response.AuthResponse;
import com.me.kaffe.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController - Authentication Presentation Layer
 * Handles HTTP requests for login and registration
 * 
 * Flow: HTTP Request -> Controller -> Service -> Repository
 * - Controller: HTTP handling, request/response mapping with DTOs
 * - Service: Business logic processing
 * - Repository: Data access
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private static final int AUTH_COOKIE_MAX_AGE_SECONDS = 24 * 60 * 60;
    private static final String JWT_COOKIE_NAME = "jwt";

    private final AuthService authService;

    /**
     * Display login page
     * GET /login
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password.");
            log.debug("Login error: Invalid credentials");
        }
        if (logout != null) {
            model.addAttribute("logout", "You have been logged out successfully.");
            log.debug("User logout successful");
        }
        return "login";
    }

    /**
     * Process login request
     * POST /login
     * 
     * Flow: Request DTO -> AuthService -> Response DTO -> Cookie
     */
    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest request,
                        HttpServletResponse response) {
        log.info("Login attempt for user: {}", request.getUsername());

        try {
            // Validate request DTO
            request.validate();

            // Call service layer (repository -> service flow)
            AuthResponse authResponse = authService.login(request);

            // Check authentication result
            if (!authResponse.isSuccess()) {
                log.warn("Login failed for user: {} - {}", request.getUsername(), authResponse.getErrorCode());
                return "redirect:/login?error";
            }

            // Set JWT token in secure HTTP-only cookie
            response.addCookie(createAuthCookie(authResponse.getToken()));
            log.info("Login successful for user: {}", request.getUsername());
            return "redirect:/";

        } catch (IllegalArgumentException e) {
            log.warn("Login validation error: {}", e.getMessage());
            return "redirect:/login?error";
        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            return "redirect:/login?error";
        }
    }

    /**
     * Display registration page
     * GET /register
     */
    @GetMapping("/register")
    public String registerPage(@RequestParam(value = "error", required = false) String error,
                               Model model) {
        if (error != null) {
            model.addAttribute("error", "Username already exists. Please choose another.");
            log.debug("Registration error: Username already exists");
        }
        return "register";
    }

    /**
     * Process registration request
     * POST /register
     * 
     * Flow: Request DTO -> AuthService -> Response DTO -> Cookie
     */
    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest request,
                           HttpServletResponse response) {
        log.info("Registration attempt for user: {}", request.getUsername());

        try {
            // Validate request DTO
            request.validate();

            // Call service layer (repository -> service flow)
            AuthResponse authResponse = authService.register(request);

            // Check registration result
            if (!authResponse.isSuccess()) {
                log.warn("Registration failed for user: {} - {}", request.getUsername(), authResponse.getErrorCode());
                return "redirect:/register?error";
            }

            // Set JWT token in secure HTTP-only cookie
            response.addCookie(createAuthCookie(authResponse.getToken()));
            log.info("Registration successful for user: {}", request.getUsername());
            return "redirect:/";

        } catch (IllegalArgumentException e) {
            log.warn("Registration validation error: {}", e.getMessage());
            return "redirect:/register?error";
        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            return "redirect:/register?error";
        }
    }

    /**
     * Process logout request
     * GET /logout
     */
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        log.info("User logout requested");
        response.addCookie(clearAuthCookie());
        return "redirect:/login?logout";
    }

    /**
     * Create secure JWT token cookie
     * 
     * @param token JWT token string
     * @return Cookie with secure settings
     */
    private Cookie createAuthCookie(String token) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, token);
        cookie.setHttpOnly(true);  // Prevents JavaScript access
        cookie.setPath("/");        // Available for entire application
        cookie.setMaxAge(AUTH_COOKIE_MAX_AGE_SECONDS);  // 24 hours
        // In production, set: cookie.setSecure(true);
        return cookie;
    }

    /**
     * Clear JWT token cookie (for logout)
     * 
     * @return Cookie with maxAge=0 (deleted immediately)
     */
    private Cookie clearAuthCookie() {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // Delete immediately
        return cookie;
    }
}
