package com.me.kaffe.controller;

import com.me.kaffe.dto.request.LoginRequest;
import com.me.kaffe.dto.request.RegisterRequest;
import com.me.kaffe.dto.response.AuthResponse;
import com.me.kaffe.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private static final int AUTH_COOKIE_MAX_AGE_SECONDS = 24 * 60 * 60;

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password.");
        }
        if (logout != null) {
            model.addAttribute("logout", "You have been logged out successfully.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest request,
                        HttpServletResponse response) {
        request.validate();
        AuthResponse result = authService.login(request);
        if (!result.isSuccess()) {
            return "redirect:/login?error";
        }

        response.addCookie(createAuthCookie(result.getToken()));
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        response.addCookie(clearAuthCookie());
        return "redirect:/login?logout";
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam(value = "error", required = false) String error,
                               Model model) {
        if (error != null) {
            model.addAttribute("error", "Username already exists. Please choose another.");
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest request,
                           HttpServletResponse response) {
        request.validate();
        AuthResponse result = authService.register(request);
        if (!result.isSuccess()) {
            return "redirect:/register?error";
        }

        response.addCookie(createAuthCookie(result.getToken()));
        return "redirect:/";
    }

    private Cookie createAuthCookie(String token) {
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(AUTH_COOKIE_MAX_AGE_SECONDS);
        return cookie;
    }

    private Cookie clearAuthCookie() {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }
}
