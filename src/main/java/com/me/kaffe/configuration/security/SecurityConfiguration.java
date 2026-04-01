package com.me.kaffe.configuration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.me.kaffe.configuration.security.impl.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/menu", "/about", "/shipment", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/categories", "/products").hasRole("ADMIN")
                        .requestMatchers("/categories/new", "/categories/*/edit", "/categories/*/delete").hasRole("ADMIN")
                        .requestMatchers("/products/new", "/products/*/edit", "/products/*/delete").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/categories", "/categories/*", "/products", "/products/*").hasAnyRole("ADMIN", "CUSTOMER", "EMPLOYEE")
                        .requestMatchers("/api/**").hasAnyRole("ADMIN", "CUSTOMER", "EMPLOYEE")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
